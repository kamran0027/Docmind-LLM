package com.kamran.Docmind.services;

import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomeOidcUserService implements OAuth2UserService<OidcUserRequest,OidcUser>{

        private final OidcUserService oidcUserService = new OidcUserService();

        private final UserService userService;

        CustomeOidcUserService(UserService userService){
            this.userService=userService;
        }

    @Override
    public @Nullable OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser=oidcUserService.loadUser(userRequest);

        String provider=userRequest.getClientRegistration().getClientId();

        userService.registerOrUpdate(provider, oidcUser);

        return oidcUser;

    }


}
