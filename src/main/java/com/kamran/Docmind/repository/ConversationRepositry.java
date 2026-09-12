package com.kamran.Docmind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamran.Docmind.Entity.Conversation;
import com.kamran.Docmind.Entity.User;

public interface ConversationRepositry extends JpaRepository<Conversation,String> {

    List<Conversation> findByUserEmailAndTemporaryFalseOrderByUpdatedAtDesc(String email);
    
    List<Conversation> findByTemporary(boolean temporary);

}
