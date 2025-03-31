package com.example.backend.utils;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    public User findUserByToken(String token){
        String email = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(email);
    }
}
