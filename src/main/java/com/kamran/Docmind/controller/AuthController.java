package com.kamran.Docmind.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kamran.Docmind.DTO.UserLogIn;
import com.kamran.Docmind.DTO.UserRegistration;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userLogin", new UserLogIn());
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "registration";
    }

}
