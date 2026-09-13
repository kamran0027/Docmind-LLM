package com.kamran.Docmind.services;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler{

    private final JwtService jwtService;

    OAuth2SuccessHandler(JwtService jwtService){
        this.jwtService=jwtService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

            OAuth2User aOAuth2User=(OAuth2User) authentication.getPrincipal();

            String email=aOAuth2User.getAttribute("email");
            String token=jwtService.generateToken(email);
            
            Cookie cookie=new Cookie("JWT",token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production 
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60* 60);
            response.addCookie(cookie);
            response.sendRedirect("/dashboard");
    }

}
