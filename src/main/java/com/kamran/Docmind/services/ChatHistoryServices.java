package com.kamran.Docmind.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kamran.Docmind.Entity.ChatMessage;
import com.kamran.Docmind.Entity.Conversation;
import com.kamran.Docmind.Entity.MessageType;
import com.kamran.Docmind.repository.ChatMessageRepository;
import com.kamran.Docmind.repository.ConversationRepositry;

import jakarta.transaction.Transactional;

@Service
public class ChatHistoryServices{

    private ConversationRepositry conversationRepositry;

    private ChatMessageRepository chatMessageRepository;

    public ChatHistoryServices(ConversationRepositry conversationRepositry, ChatMessageRepository chatMessageRepository) {
        this.conversationRepositry = conversationRepositry;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional
    public Conversation createConversation(boolean temporary){
        Conversation conversation=new Conversation();

        LocalDateTime time=LocalDateTime.now();
        conversation.setId(UUID.randomUUID().toString());
        conversation.setTitle("new Chat");
        conversation.setTemporary(temporary);
        conversation.setCreatedAt(time);
        conversation.setUpdatedAt(time);

        return conversationRepositry.save(conversation);

    }

    @Transactional
    public void saveUserMessage(String conversationId, String userRequest){
        saveMessage(conversationId, userRequest,MessageType.USER);
    }

    @Transactional
    public void saveAssistantMessage(String conversationId,String response){
        saveMessage(conversationId, response,MessageType.ASSISTANT);
    }

    public void saveMessage(String conversationId, String message, MessageType messageType) {
        Conversation conversation = conversationRepositry.findById(conversationId).orElseThrow(() -> 
                                                            new RuntimeException("Conversation not found"));
        
        Long nextSequenceId = chatMessageRepository.findMaxSequenceId(conversationId) + 1;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversation(conversation);
        chatMessage.setContent(message);
        chatMessage.setType(messageType);
        chatMessage.setSequenceId(nextSequenceId);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepositry.save(conversation);

    }
    
}
