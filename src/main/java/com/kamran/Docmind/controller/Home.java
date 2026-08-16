package com.kamran.Docmind.controller;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamran.Docmind.DTO.UserRequest;
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

    @PostMapping("/ask")
    public ChatResponde tempAsk(@RequestParam("temporary-chat") Boolean temporary, @RequestBody UserRequest request){
        if(temporary){
            String conversationId=UUID.randomUUID().toString();
            String response=chatClient.prompt()
                                    .user(request.getUserInput())
                                    .advisors(advisor ->
                                            advisor.param(ChatMemory.CONVERSATION_ID,conversationId)

                                    )
                                    .call()
                                    .content();
        return new ChatResponde(conversationId,response);
        }
        System.out.println("Regular request received: " + request.getUserInput());

        String conversationId=UUID.randomUUID().toString();

        String response=chatClient.prompt()
                                    .user(request.getUserInput())
                                    .advisors(advisor ->
                                            advisor.param(ChatMemory.CONVERSATION_ID,conversationId)

                                    )
                                    .call()
                                    .content();
        return new ChatResponde(conversationId,response);
    }

    @PostMapping("/ask/{conversationId}")
    public ChatResponde processPromt(@PathVariable String conversationId, @RequestBody UserRequest userRequest){

        
         System.out.println(
                "Conversation ID: "
                + conversationId);

        System.out.println(
                "User: "
                + userRequest.getUserInput()
        );


        String response=chatClient.prompt()
                                    .user(userRequest.getUserInput())
                                    .advisors(advisor ->
                                        advisor.param(ChatMemory.CONVERSATION_ID, conversationId)
                                    ).call().content();
        return new ChatResponde(conversationId,response);
    }

}
