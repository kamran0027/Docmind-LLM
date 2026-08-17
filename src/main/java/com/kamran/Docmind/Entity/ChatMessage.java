package com.kamran.Docmind.Entity;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Table(
    name = "chat_messages",
    indexes = {
        @Index(name="idx_conversation_id",columnList = "conversation_id")

    }
)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name = "conversation_id",nullable = false)
    private Conversation conversation;

    @Column(
        name = "content",
        columnDefinition = "TEXT",
        nullable = false
    )
    private String content;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private MessageType type;

    @Column(
        name = "timestamp",
        nullable = false
    )
    private LocalDateTime timestamp;

    @Column(
        name = "sequence_id",
        nullable = false
    )
    private Long sequenceId;



}
