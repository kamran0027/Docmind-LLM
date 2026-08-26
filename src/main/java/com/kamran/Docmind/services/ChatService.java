package com.kamran.Docmind.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kamran.Docmind.DTO.ConversationDto;
import com.kamran.Docmind.DTO.MessageDto;
import com.kamran.Docmind.Entity.Conversation;
import com.kamran.Docmind.repository.ChatMessageRepository;
import com.kamran.Docmind.repository.ConversationRepositry;

@Service
public class ChatService {

    private final ConversationRepositry conversationRepositry;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ConversationRepositry conversationRepositry, ChatMessageRepository chatMessageRepository) {
        this.conversationRepositry = conversationRepositry;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<MessageDto> getConversationMessages(String conversationId) {
        Conversation conversation = conversationRepositry.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        if (conversation.isTemporary()) {
            throw new RuntimeException("Cannot retrieve messages for a temporary conversation");
        }
        return chatMessageRepository.findByConversationIdOrderBySequenceIdDesc(conversationId)
                .stream()
                .map(message -> new MessageDto(conversationId,message.getType(), message.getContent(), message.getTimestamp()))
                .collect(Collectors.toList());
    }

    public void deleteConversation(String conversationId) {
        Conversation conversation = conversationRepositry.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        if (conversation.isTemporary()) {
            throw new RuntimeException("Cannot delete a temporary conversation");
        }
        conversation.setTemporary(true);
        conversationRepositry.save(conversation);
    }

    public List<ConversationDto> getAllConversation(String userEmail){

        List<Conversation>conversations= conversationRepositry.findByTemporary(false);

        List<ConversationDto> conversationDtos=conversations.stream()
                .map(conversation -> new ConversationDto(conversation.getId(), conversation.isTemporary(), conversation.getTitle(), conversation.getCreatedAt().toString()))
                .collect(Collectors.toList());

        return conversationDtos;

    }
    
}
