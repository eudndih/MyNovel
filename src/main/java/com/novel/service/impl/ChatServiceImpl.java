package com.novel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novel.domain.dto.ChatDTO;
import com.novel.domain.vo.ChatVO;
import com.novel.domain.vo.SessionVO;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.result.Result;
import com.novel.service.ChatService;
import com.novel.utils.UserContext;
import com.novel.utils.chatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;

import static com.novel.constant.redisConstant.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;
    private final chatUtils chatUtils;

    @Override
    public Flux<String> continueWrite(ChatDTO dto) {
        // 会话 ID：同一本书同一章节共享一份记忆
        String chatId = dto.getNovelId() + ":" + dto.getChapterId();

        // 把标题 + 已有正文 + 用户指令拼成真正的 prompt
        String userPrompt = buildPrompt(dto);

        return chatUtils.chat(userPrompt,chatId);
    }

    @Override
    public synchronized Result<SessionVO> createSession(ChatDTO dto) {
        Long userId = UserContext.getUserId();
        String sessionsKey = AI_USER_SESSIONS_KEY_PREFIX + userId;
        try {
            Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                    .reverseRangeWithScores(sessionsKey, 0, 0);
            if (typedTuples != null && !typedTuples.isEmpty()) {
                String oldSessionId = typedTuples.iterator().next().getValue();
                if (oldSessionId != null && !Boolean.TRUE.equals(stringRedisTemplate.hasKey(
                        AI_SESSION_MESSAGES_KEY_PREFIX + oldSessionId))) {
                    String title = (String) stringRedisTemplate.opsForHash().get(
                            AI_SESSION_INFO_KEY_PREFIX + oldSessionId, AI_SESSION_TITLE_FIELD);
                    return Result.success(new SessionVO(oldSessionId,
                            title == null ? "新的对话" : title, null));
                }
            }

            String sessionId = UUID.randomUUID().toString();
            stringRedisTemplate.opsForZSet().add(sessionsKey, sessionId, System.currentTimeMillis());
            return Result.success(new SessionVO(sessionId, "新的对话", LocalDateTime.now()));
        } catch (Exception e) {
            throw new BusinessException("新建会话失败");
        }
    }

    @Override
    public Result<List<SessionVO>> getSessions() {
        Long userId = UserContext.getUserId();
        Set<String> set = stringRedisTemplate.opsForZSet().reverseRange(AI_USER_SESSIONS_KEY_PREFIX + userId, 0, -1);
        if (set == null || set.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        ArrayList<SessionVO> list = new ArrayList<>();
        for(String sessionId : set) {
            SessionVO sessionVO = new SessionVO();
            String s = stringRedisTemplate.opsForList().getFirst(AI_SESSION_MESSAGES_KEY_PREFIX + sessionId);
            sessionVO.setId(sessionId);
            sessionVO.setTitle((String) stringRedisTemplate.opsForHash().get(AI_SESSION_INFO_KEY_PREFIX + sessionId, AI_SESSION_TITLE_FIELD));
            if (s != null&&(String) stringRedisTemplate.opsForHash().get(AI_SESSION_INFO_KEY_PREFIX + sessionId, AI_SESSION_TITLE_FIELD)==null) {
                try {
                    ChatVO vo = objectMapper.readValue(s, ChatVO.class);
                    String content = vo.getContent();
                    if(content.length()>10){
                        sessionVO.setTitle(content.substring(0,10));
                    }else{
                        sessionVO.setTitle(content);
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            list.add(sessionVO);
        }
        return Result.success(list);
    }

    @Override
    public Result<ChatVO> chat(String sessionId, String prompt) {
        saveChat(sessionId,"USER",prompt);
        String content =chatUtils.chatBySession(sessionId,prompt)
                .collectList()
                .map(parts -> String.join("", parts))
                .block();
        if (content == null || content.isBlank()) {
            throw new BusinessException("AI 未返回有效内容");
        }
        ChatVO chatVO = new ChatVO();
        chatVO.setRole("ASSISTANT");
        chatVO.setSessionId(sessionId);
        chatVO.setContent(content);
        chatVO.setTime(LocalDateTime.now());
        //调用保存会话消息
        saveChat(sessionId,"ASSISTANT",content);
        return Result.success(chatVO);
    }

    @Override
    public Result<List<ChatVO>> getChats(String sessionId) {
        Long userId = UserContext.getUserId();
        List<String> list = stringRedisTemplate.opsForList().range(AI_SESSION_MESSAGES_KEY_PREFIX + sessionId, 0, -1);
        if (list == null || list.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        ArrayList<ChatVO> vos = new ArrayList<>();
        for(String json: list) {
            try {
                ChatVO vo = objectMapper.readValue(json, ChatVO.class);
                vos.add(vo);
            } catch (JsonProcessingException e) {
                throw new BusinessException("json转对象失败");
            }
        }
    return Result.success(vos);
    }

    @Override
    @Transactional
    public Result<Void> deleteSession(String sessionId) {
        stringRedisTemplate.opsForZSet().remove(AI_USER_SESSIONS_KEY_PREFIX + UserContext.getUserId(), sessionId);
        stringRedisTemplate.delete(Arrays.asList(AI_SESSION_INFO_KEY_PREFIX + sessionId,
                AI_SESSION_MESSAGES_KEY_PREFIX + sessionId));
        return Result.success();
    }

    @Override
    public Result<Void> updateSessionTitle(String sessionId, String title) {
        String key = AI_SESSION_INFO_KEY_PREFIX + sessionId;
        stringRedisTemplate.opsForHash().put(key, AI_SESSION_TITLE_FIELD, title);
        return Result.success();
    }

    private void saveChat(String sessionId, String role, String content) {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", role);
        message.put("content", content);
        message.put("time", LocalDateTime.now().toString());
        try {
            String key = AI_SESSION_MESSAGES_KEY_PREFIX + sessionId;
            stringRedisTemplate.opsForList().rightPush(key, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new BusinessException("保存会话消息失败");
        }
    }

    private String buildPrompt(ChatDTO dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            sb.append("【章节标题】").append(dto.getTitle()).append("\n");
        }
        if (dto.getText() != null && !dto.getText().isBlank()) {
            sb.append("【已写正文】\n").append(dto.getText()).append("\n");
        }
        sb.append("【续写要求】").append(dto.getPrompt());
        return sb.toString();
    }
}



