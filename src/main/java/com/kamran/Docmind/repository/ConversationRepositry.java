package com.kamran.Docmind.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamran.Docmind.Entity.Conversation;


public interface ConversationRepositry extends JpaRepository<Conversation,String> {

    List<Conversation> findByUserEmailAndTemporaryFalseOrderByUpdatedAtDesc(String email);
    
    List<Conversation> findByTemporary(boolean temporary);

    void deleteByIdAndUserEmail(String conversationId, String email);

    Optional<Conversation> findByIdAndUserEmail(String conversationId, String email);



}
