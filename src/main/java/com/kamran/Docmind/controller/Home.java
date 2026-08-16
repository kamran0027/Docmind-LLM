package com.kamran.Docmind.controller;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamran.Docmind.DTO.UserRequest;
import com.kamran.Docmind.Entity.Conversation;
import com.kamran.Docmind.record.ChatResponde;
import com.kamran.Docmind.services.ChatHistoryServices;

@RestController
@RequestMapping("/")
public class Home {

    private final ChatClient chatClient;
    private final ChatHistoryServices chatHistoryServices;

    Home(ChatClient chatClient, ChatHistoryServices chatHistoryServices) {
        this.chatClient = chatClient;
        this.chatHistoryServices = chatHistoryServices;
    }

    @GetMapping
    public String home() {

        return "home";
    }

    @PostMapping("/ask")
    public ChatResponde tempAsk(@RequestParam(value = "temporary-chat",defaultValue = "false") Boolean temporary, @RequestBody UserRequest request) {

        Conversation conversation = chatHistoryServices.createConversation(temporary);
        String conversationId = conversation.getId();

        // savinf user message
        chatHistoryServices.saveUserMessage(conversationId, request.getUserInput());

        String response = chatClient.prompt()
                .user(request.getUserInput())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        // saving assistent message
        chatHistoryServices.saveAssistantMessage(conversationId, response);
        return new ChatResponde(conversationId, response);

    }

    @PostMapping("/ask/{conversationId}")
    public ChatResponde processPromt(@PathVariable String conversationId, @RequestBody UserRequest userRequest) {

        System.out.println(
                "Conversation ID: "
                        + conversationId);

        System.out.println(
                "User: "
                        + userRequest.getUserInput());
        // saving user message
        chatHistoryServices.saveUserMessage(conversationId, userRequest.getUserInput());

        String response = chatClient.prompt()
                .user(userRequest.getUserInput())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId)).call().content();

        // saving assistant message
        chatHistoryServices.saveAssistantMessage(conversationId, response);
        return new ChatResponde(conversationId, response);
    }

    @PostMapping("/ask-2")
    public ChatResponse testChatResponde(@RequestBody UserRequest request) {

        String conversationId = UUID.randomUUID().toString();

        return chatClient.prompt()
                .user(request.getUserInput())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

    }

}
