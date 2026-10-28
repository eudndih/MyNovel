package com.novel.controller;

import com.novel.domain.dto.ChatDTO;
import com.novel.domain.dto.ChatRequest;
import com.novel.domain.vo.ChatVO;
import com.novel.domain.vo.SessionVO;
import com.novel.result.Result;
import com.novel.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("api/ai")
@RequiredArgsConstructor
@Tag(name = "AI对话相关接口")
public class chatController {
    private final ChatService chatService;

    @PostMapping("continue-writing")
    @Operation(description = "文章续写功能")
    public Flux<String> continueWrite(@RequestBody ChatDTO dto) {
        return chatService.continueWrite(dto);
    }

    @PostMapping("sessions")
    @Operation(description = "新建会话功能")
    public Result<SessionVO> createSession(@RequestBody ChatDTO dto) {
        return chatService.createSession(dto);
    }

    @GetMapping("sessions")
    @Operation(description = "获取会话列表功能")
    public Result<List<SessionVO>> getSessions() {
        return chatService.getSessions();
    }

    @PostMapping("chat")
    @Operation(description = "与AI聊天功能")
    public Result<ChatVO> chat(@RequestBody ChatRequest request) {
        String SessionId = request.getSessionId();
        String message = request.getMessage();
        System.out.println(SessionId + "-" + message);
        return chatService.chat(SessionId, message);
    }

    @GetMapping("sessions/{sessionId}/messages")
    @Operation(description = "获取会话信息")
    public Result<List<ChatVO>> getChats(@PathVariable("sessionId") String sessionId) {
        return chatService.getChats(sessionId);
    }

    @DeleteMapping("sessions/{sessionId}")
    @Operation(description = "删除会话")
    public Result<Void> deleteSession(@PathVariable("sessionId") String sessionId) {
        return chatService.deleteSession(sessionId);
    }

    @PutMapping("sessions/{sessionId}/title")
    @Operation(description = "重命名")
    public Result<Void> updateSessionTitle(@PathVariable("sessionId")String sessionId, @RequestBody String title) {
        return chatService.updateSessionTitle(sessionId,title);
    }
}
