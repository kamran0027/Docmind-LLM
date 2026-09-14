package com.kamran.Docmind.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kamran.Docmind.DTO.UserRegistration;
import com.kamran.Docmind.Entity.Role;
import com.kamran.Docmind.Entity.User;
import com.kamran.Docmind.Entity.UserType;
import com.kamran.Docmind.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }

    public boolean isRegister(String email){
        return userRepository.findByEmail(email).isPresent()?true:false;
    }

    public boolean register(UserRegistration userRegistration){
        User user=new User();
        user.setEmail(userRegistration.getEmail());
        user.setName(userRegistration.getName());
        user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
        user.setProvider("NONE");
        user.setRole(Role.USER);
        user.setUserType(UserType.NORMAL);

        userRepository.save(user);
        return true;
    }

    public boolean sendToDashBoard(String email,HttpServletResponse response){
        String token=jwtService.generateToken(email);
        Cookie cookie=new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }

    public boolean login(String email,String password){
        User user=userRepository.findByEmail(email).orElse(null);
        if(user==null){
            return false;
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            return false;
        }
        return true;
    }

    public boolean updatePassword(String email,String newPassword){
        User user=userRepository.findByEmail(email).orElse(null);
        if(user==null){
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

}
