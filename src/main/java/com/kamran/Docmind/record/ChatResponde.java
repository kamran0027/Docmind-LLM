package com.kamran.Docmind.record;

import org.springframework.ai.chat.messages.MessageType;

public record ChatResponde(MessageType messageType,String content) {

}
