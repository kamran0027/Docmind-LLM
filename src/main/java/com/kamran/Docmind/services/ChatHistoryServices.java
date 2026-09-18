package com.kamran.Docmind.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kamran.Docmind.DTO.ConversationDto;
import com.kamran.Docmind.Entity.ChatMessage;
import com.kamran.Docmind.Entity.Conversation;
import com.kamran.Docmind.Entity.MessageType;
import com.kamran.Docmind.repository.ChatMessageRepository;
import com.kamran.Docmind.repository.ConversationRepositry;
import com.kamran.Docmind.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ChatHistoryServices{

    private ConversationRepositry conversationRepositry;

    private ChatMessageRepository chatMessageRepository;

    private UserRepository userRepository;

    public ChatHistoryServices(ConversationRepositry conversationRepositry, ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.conversationRepositry = conversationRepositry;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Conversation createConversation(boolean temporary){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Conversation conversation=new Conversation();
        userRepository.findByEmail(auth.getName())
                    .ifPresent(conversation::setUser);

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

    public List<ConversationDto> getConversationByUserEmail(String email){
        
        List<Conversation> conversations=conversationRepositry.findByUserEmailAndTemporaryFalseOrderByUpdatedAtDesc(email);
        List<ConversationDto> dtos=conversations.stream()
                                    .map( conversation ->{
                                        ConversationDto dto =new ConversationDto();
                                        dto.setId(conversation.getId());
                                        dto.setTitle(conversation.getTitle());
                                        dto.setCreatedAt(conversation.getCreatedAt().toString());

                                        return dto;
                                    }).toList();
        return dtos;
    }

    @Transactional 
    public void deleteConversation(String conversationId){ 
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation=conversationRepositry.findByIdAndUserEmail(conversationId,email)
                                        .orElseThrow(()->new RuntimeException("Conversation not found or user not authorized to delete it"));
        conversationRepositry.delete(conversation);
    }
    
}
