package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/login/auth/google")
public class GoogleAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> googleLogin(@RequestBody GoogleAuthRequest request) {
        String accessToken = request.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Access token is required");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> response;
        try {
            response = restTemplate.exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.POST, entity, GoogleUserResponse.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google access token");
        }

        GoogleUserResponse googleUser = response.getBody();
        if (googleUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve user info");
        }

        String email = googleUser.getEmail();
        if(userRepository.existsByEmail(email)){
            String token = jwtTokenProvider.createToken(email);
            return ResponseEntity.ok(new AuthResponse(token,false));
        }
        else {
            String name = googleUser.getName();
            userRepository.save(new User(email,name,passwordEncoder.encode("google_auth_user")));
            String token = jwtTokenProvider.createToken(email);
            return ResponseEntity.ok(new AuthResponse(token,true));
        }
    }
}

class GoogleAuthRequest {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

class GoogleUserResponse {
    private String email;
    private String name;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

class AuthResponse {
    private String token;
    private Boolean isNewUser;

    public AuthResponse(String token, Boolean isNewUser){
        this.token = token;
        this.isNewUser = isNewUser;
    }
    public String getToken() {
        return token;
    }
}
