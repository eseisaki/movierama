package com.personal.movierama_backend.auth.serivce;

import com.personal.movierama_backend.auth.dto.LoginRequest;
import com.personal.movierama_backend.auth.dto.SignupRequest;

public interface AuthService {
    String registerUser(SignupRequest signupRequest);
    String authenticateUser(LoginRequest loginRequest);
}
