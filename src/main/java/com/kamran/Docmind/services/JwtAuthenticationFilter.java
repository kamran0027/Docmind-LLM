package com.kamran.Docmind.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component

public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final CustomeUserDetailsService customeUserDetailsService;

    JwtAuthenticationFilter(JwtService jwtService,CustomeUserDetailsService customeUserDetailsService){
        this.jwtService = jwtService;
        this.customeUserDetailsService=customeUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                    HttpServletResponse response, FilterChain filterChain) throws ServletException,IOException{


        // in this we first extract jwt token  from the header if it is not present then we extract from the Cookies
        String authHeader=request.getHeader("Authorization");

        if((authHeader==null || !authHeader.startsWith("Bearer ")) && extractJwtFromCookies(request)==null){

            filterChain.doFilter(request, response);
            return;

        }
        String token="";
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            token+=authHeader.substring(7);
        }
        else{
            token=token+extractJwtFromCookies(request);
        }

        try {
            
            String email=jwtService.extractEmail(token);
            if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails user=customeUserDetailsService.loadUserByUsername(email);

                if(jwtService.validateToken(token,user)){

                    UsernamePasswordAuthenticationToken authentication=
                            new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } 

        catch (Exception e) {


            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
        
    }

    private String extractJwtFromCookies(HttpServletRequest request){
        if(request.getCookies()==null){
            return null;
        }
        for(Cookie cookie:request.getCookies()){
            if("JWT".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

    

}
