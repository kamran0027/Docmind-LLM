package com.kamran.Docmind.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                                        .chatMemoryRepository(jdbcChatMemoryRepository)
                                        .maxMessages(20)
                                        .build();
    }
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatBuilder, ChatMemory chatMemory){

        return chatBuilder.defaultAdvisors(
                                        MessageChatMemoryAdvisor
                                            .builder(chatMemory)
                                            .build()
                                    ).build();
    }



}
