package com.kamran.Docmind.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kamran.Docmind.DTO.ProfileDto;
import com.kamran.Docmind.services.UserService;

@Controller
@RequestMapping("/user")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model,Authentication authentication) {
        String userEmail = authentication.getName(); // Get the email of the logged-in user
        ProfileDto profileDto = userService.getProfile(userEmail);
        model.addAttribute("profile", profileDto);
        return "profile";
    }

}
