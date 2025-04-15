package com.example.backend.service;

import com.example.backend.controller.*;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtTokenProvider;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.backend.dto.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse googleLogin(GoogleAuthRequest request) {
        String accessToken = request.getAccessToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.POST,
                    entity,
                    GoogleUserResponse.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Google access token");
        }

        GoogleUserResponse googleUser = responseEntity.getBody();
        if (googleUser == null) {
            throw new RuntimeException("Failed to retrieve user info");
        }

        String email = googleUser.getEmail();
        if (userRepository.existsByEmail(email)) {
            User user = userRepository.findByEmail(email);
            String token = jwtTokenProvider.createToken(email);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);
            return new AuthResponse(token, refreshToken, false, user.getUsername(), email);
        } else {
            String name = googleUser.getName();
            userRepository.save(new User(name, email, passwordEncoder.encode("google_auth_user")));
            String token = jwtTokenProvider.createToken(email);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);
            return new AuthResponse(token, refreshToken, true, name, email);
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request)
        {
            String refreshToken = request.getRefreshToken();
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            String newAccessToken = jwtTokenProvider.createToken(email);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

            return new AuthResponse(newAccessToken, newRefreshToken, false, null, email);
        }

    @Override
    public String verify(VerifyTokenRequest request){
        String token = request.getJstToken();
        return jwtTokenProvider.validateToken(token) ? "Valid" : "Invalid";
    }
}
