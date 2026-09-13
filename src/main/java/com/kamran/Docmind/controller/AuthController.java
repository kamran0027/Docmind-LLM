package com.kamran.Docmind.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kamran.Docmind.DTO.UserLogIn;
import com.kamran.Docmind.DTO.UserRegistration;
import com.kamran.Docmind.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userLogin", new UserLogIn());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute UserLogIn userLogIn, 
                                RedirectAttributes redirectAttributes,
                                HttpServletResponse response) {

        if (!authService.isRegister(userLogIn.getEmail())) {
            // Handle login logic
            redirectAttributes.addFlashAttribute("error", "Email not registered");
            return "redirect:/auth/login";
        }
        if(authService.login(userLogIn.getEmail(), userLogIn.getPassword())){
            if(authService.sendToDashBoard(userLogIn.getEmail(), response)){
                redirectAttributes.addFlashAttribute("success", "Login successful");
                return "redirect:/dashboard";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        return "redirect:/auth/login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "registration";
    }
    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute UserRegistration userRegistration
                    ,RedirectAttributes redirectAttributes,HttpServletResponse response) {
        if(authService.isRegister(userRegistration.getEmail())){
            redirectAttributes.addFlashAttribute("error", "Email already registered");
            return "redirect:/auth/registration";
        }
        if(authService.register(userRegistration)){
            if(authService.sendToDashBoard(userRegistration.getEmail(), response)){
                System.out.println("***************************************************");
                System.out.println("Registration successful for email: " + userRegistration.getEmail() + ", name: " + userRegistration.getName());
                System.out.println("***************************************************");
                redirectAttributes.addFlashAttribute("success", "Registration successful");
                return "redirect:/dashboard";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Registration failed");
        return "redirect:/auth/registration";
    }

}
