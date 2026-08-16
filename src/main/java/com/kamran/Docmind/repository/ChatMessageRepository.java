package com.kamran.Docmind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kamran.Docmind.Entity.ChatMessage;


public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{

    List<ChatMessage> findByConversationIdOrderBySequenceIdDesc(String conversationId);

    @Query("""
        SELECT COALESCE(MAX(c.sequenceId), 0)
        FROM ChatMessage c
        WHERE c.conversation.id = :conversationId
    """)
    Long findMaxSequenceId(
        @Param("conversationId") String conversationId
    );

}
