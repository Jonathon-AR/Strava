package com.example.backend.service;
import com.example.backend.dto.*;

public interface AuthService {
    AuthResponse googleLogin(GoogleAuthRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    String verify(VerifyTokenRequest token);
}
