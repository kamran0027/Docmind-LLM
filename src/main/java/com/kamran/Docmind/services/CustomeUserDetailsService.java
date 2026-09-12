package com.kamran.Docmind.services;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.kamran.Docmind.Entity.User;
import com.kamran.Docmind.repository.UserRepository;

@Component
public class CustomeUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    CustomeUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepository.findByEmail(username).orElseThrow(()-> 
                                 new UsernameNotFoundException("user not found with username : "+username ));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword() !=null?user.getPassword() :"")
                .authorities(List.of(
                                    new SimpleGrantedAuthority(
                                        "ROLE_"+user.getRole().name()
                                    ),
                                    new SimpleGrantedAuthority(
                                        "USER_TYPE_"+user.getUserType().name()
                                    )
                ))
                .build();
    }

}
