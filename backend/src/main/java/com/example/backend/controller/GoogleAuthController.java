package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.backend.security.jwt.JwtTokenProvider;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.http.*;
import java.util.Optional;

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

        System.out.println(googleUser.toString());
        String email = googleUser.getEmail();
        String name = googleUser.getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = optionalUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setPassword(passwordEncoder.encode("google_auth_user")); // Placeholder password
            return userRepository.save(newUser);
        });

        String token = jwtTokenProvider.createToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
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

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
