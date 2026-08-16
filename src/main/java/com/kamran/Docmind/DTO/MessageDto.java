package com.kamran.Docmind.DTO;

import java.time.LocalDateTime;

import com.kamran.Docmind.Entity.MessageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MessageDto {

    private MessageType type;
    private String content;
    private LocalDateTime timestamp;

}
