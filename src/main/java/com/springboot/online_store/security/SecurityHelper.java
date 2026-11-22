package com.springboot.online_store.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {
    public String getCurrentName(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new RuntimeException("User not authenticated");
        }
        return auth.getName();
    }
}
