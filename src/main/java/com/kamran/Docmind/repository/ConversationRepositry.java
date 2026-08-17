package com.kamran.Docmind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamran.Docmind.Entity.Conversation;

public interface ConversationRepositry extends JpaRepository<Conversation,String> {

    List<Conversation> findByTemporary(boolean temporary);
}
