package com.kamran.Docmind.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
                .map(message -> new MessageDto(message.getType(), message.getContent(), message.getTimestamp()))
                .collect(Collectors.toList());
    }
    
}
