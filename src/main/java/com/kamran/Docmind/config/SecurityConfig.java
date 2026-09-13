package com.kamran.Docmind.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kamran.Docmind.services.CustomeOidcUserService;
import com.kamran.Docmind.services.JwtAuthenticationFilter;
import com.kamran.Docmind.services.OAuth2SuccessHandler;

import jakarta.servlet.http.Cookie;

@Configuration
public class SecurityConfig {

    private final CustomeOidcUserService customeOidcUserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    SecurityConfig(CustomeOidcUserService customeOidcUserService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.customeOidcUserService = customeOidcUserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        

        httpSecurity
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session ->
                                                session.sessionCreationPolicy(
                                                    SessionCreationPolicy.STATELESS
                                                )
                    )

                    .authorizeHttpRequests(
                                        auth -> 

                                        auth.requestMatchers(
                                                "/auth/**",
                                                            "/login",
                                                            "/oauth2/**",
                                                            "/css/**",
                                                            "/js/**",
                                                            "/images/**"
                                        ).permitAll()
                                        .anyRequest()
                                        .authenticated()
                    )
                    .oauth2Login( oauth-> 
                            oauth.userInfoEndpoint( userInfo-> 
                                    userInfo.oidcUserService(customeOidcUserService)
                                )
                                .successHandler(oAuth2SuccessHandler)
                    )
                    .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)

                    // .logout(logout -> logout
                    //         .logoutUrl("/logout")
                    //         .logoutSuccessUrl("/login")
                    //         .invalidateHttpSession(true)
                    //         .clearAuthentication(true)
                    //         .deleteCookies("JESSIONID")
                    //         .permitAll()

                    // );
                    .logout(logout ->logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler(
                                    (request,response,authentication)->{
                                        Cookie cookie=new Cookie("JWT", null);
                                        cookie.setPath("/");
                                        cookie.setMaxAge(0);
                                        response.addCookie(cookie);
                                        response.sendRedirect("/auth/login");
                                    }
                                ).permitAll()

                    );
                    
        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
