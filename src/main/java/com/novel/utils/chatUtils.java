package com.novel.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class chatUtils {
    private final ChatClient chatClient;
    public  Flux<String> chat(String userPrompt,String chatId) {
        return chatClient.prompt()
                .user(userPrompt)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID, chatId  // 使用新常量
                ))
                .toolContext(Map.of("userId",UserContext.getUserId()))
                .stream()
                .content();
    }

    public Flux<String> chatBySession(String sessionId, String prompt) {
       return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID, sessionId
                ))
               .toolContext(Map.of("userId",UserContext.getUserId()))
                .stream()
                .content();
    }
}
