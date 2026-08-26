package com.kamran.Docmind.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.t;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 255)
    private String title;

    private boolean temporary;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @OneToMany(
        fetch =FetchType.LAZY,
        mappedBy = "conversation",
        cascade = CascadeType.ALL,
        orphanRemoval = true

    )
    private List<ChatMessage> messages = new ArrayList<>();


    @OneToMany(
        fetch=FetchType.LAZY,
        mappedBy="conversation",
        cascade=CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Attachment> attachments=new ArrayList<>();


     public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setConversation(this);
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setConversation(null);
    }
}