package com.kamran.Docmind.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamran.Docmind.record.ChatResponde;

@RestController
@RequestMapping("/")
public class Home {

    private final ChatClient chatClient;

    Home(ChatClient chatClient){
        this.chatClient=chatClient;
    }


    @GetMapping
    public String home(){

        return "home";
    }

    @GetMapping("/ask")
    public ChatResponde ask(@RequestParam("q") String request){

        return chatClient.prompt().user(request).call().entity(ChatResponde.class);    
    }

}
