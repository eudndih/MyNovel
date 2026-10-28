package com.novel.config;

import com.novel.tolls.novelTolls;
import com.novel.utils.UserContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AIConfig {

    /**
     * 内存会话记忆：按 conversationId 保存最近的对话消息。
     * 应用重启后记忆会清空，适合当前课程项目。
     */
    @Bean
    public MessageWindowChatMemory  messageWindowChatMemory(){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(50)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory,novelTolls novelTolls) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(novelTolls)
                .build();
    }
}
