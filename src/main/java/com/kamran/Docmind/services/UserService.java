package com.kamran.Docmind.services;

import java.util.Optional;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.kamran.Docmind.Entity.Role;
import com.kamran.Docmind.Entity.User;
import com.kamran.Docmind.Entity.UserType;
import com.kamran.Docmind.repository.UserRepository;

@Service
public class UserService {


    private final UserRepository userRepository;

    UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    public User registerOrUpdate(String provider,OidcUser oidcUser){
        String subject=oidcUser.getSubject();

        String name=oidcUser.getClaimAsString("name");
        String email=oidcUser.getClaimAsString("email");

        Optional<User> existingUser=userRepository.findByProviderAndProviderSubject(provider,subject);

        if(existingUser.isPresent()){
            User user=existingUser.get();
            user.setEmail(email);
            user.setName(name);

            return user;
        }
        User newUser=new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProvider(provider);
        newUser.setProviderSubject(subject);
        newUser.setRole(Role.USER);
        newUser.setUserType(UserType.NORMAL);

        return userRepository.save(newUser);
    }

}
