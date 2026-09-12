package com.kamran.Docmind.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kamran.Docmind.DTO.ConversationDto;
import com.kamran.Docmind.services.ChatHistoryServices;

@Controller
public class HomePageController {


    private final ChatHistoryServices chatHistoryServices;

    HomePageController(ChatHistoryServices chatHistoryServices){
        this.chatHistoryServices=chatHistoryServices;
    }



    @GetMapping("/dashboard")
    public String dashboard() {
        return "home";
    }

    @GetMapping("/chat")
    @ResponseBody
    public List<ConversationDto> loadAllchatByUserEmail(Authentication auth) {
        String email=auth.getName();
        return chatHistoryServices.getConversationByUserEmail(email);
        
    }


    

}
