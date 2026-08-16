package com.kamran.Docmind.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamran.Docmind.DTO.MessageDto;
import com.kamran.Docmind.services.ChatService;

@RestController
@RequestMapping("/c")
public class ConversationController {

    private final ChatService chatService;

    ConversationController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{conversationId}")
    public List<MessageDto> getConversationMessages(@PathVariable String conversationId) {
        return chatService.getConversationMessages(conversationId);
    }

}
