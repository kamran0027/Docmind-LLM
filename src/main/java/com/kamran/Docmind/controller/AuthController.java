package com.kamran.Docmind.controller;

import java.security.SecureRandom;

import org.checkerframework.checker.units.qual.m;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kamran.Docmind.DTO.UserLogIn;
import com.kamran.Docmind.DTO.UserRegistration;
import com.kamran.Docmind.services.AuthService;
import com.kamran.Docmind.services.EmailService;
import com.kamran.Docmind.services.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;
    
    private final EmailService emailService;
    AuthController(AuthService authService, JwtService jwtService, EmailService emailService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.emailService = emailService;
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

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@ModelAttribute("email") String email,
                                        RedirectAttributes redirectAttributes) {

        boolean userExist=authService.isRegister(email);

        if(!userExist){
            redirectAttributes.addFlashAttribute("error","No account found with this email");
            //model.addAttribute("error","No account found with this email");
            return "redirect:/auth/forgot-password";
        }
        String token=jwtService.generateToken(email);
        if(emailService.sendResetPasswordLink(email,token)){
            redirectAttributes.addFlashAttribute("success","Password reset link has been sent to your email");
        } else {
            redirectAttributes.addFlashAttribute("error","Failed to send reset password link");
        }
        return "redirect:/auth/login";

    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        String email=jwtService.extractEmail(token);
        if(email==null || !jwtService.isTokenValid(token)){
            redirectAttributes.addFlashAttribute("error","Invalid or expired token");
            return "redirect:/auth/login";
        }
        model.addAttribute("email", email);
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                        @RequestParam("password") String password,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        RedirectAttributes redirectAttributes) {
        String email=jwtService.extractEmail(token);
        if(email==null || !jwtService.isTokenValid(token)){
            redirectAttributes.addFlashAttribute("error","Invalid or expired token");
            return "redirect:/auth/login";
        }
        if(!password.equals(confirmPassword)){
            redirectAttributes.addFlashAttribute("error","Passwords do not match");
            return "redirect:/auth/reset-password?token=" + token;  
        }
        if(!authService.updatePassword(email, password)){
            redirectAttributes.addFlashAttribute("error","Failed to update password");
            return "redirect:/auth/reset-password?token=" + token;
        }
        redirectAttributes.addFlashAttribute("success","Password updated successfully");
        return "redirect:/auth/login";
    }


        

}