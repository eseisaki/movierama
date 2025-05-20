package com.personal.movierama_backend.auth.controller;


import com.personal.movierama_backend.auth.dto.LoginRequest;
import com.personal.movierama_backend.auth.dto.LoginResponse;
import com.personal.movierama_backend.auth.dto.SignupRequest;
import com.personal.movierama_backend.auth.dto.SignupResponse;
import com.personal.movierama_backend.auth.serivce.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        String token = authService.registerUser(request);
        return ResponseEntity.ok(new SignupResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.authenticateUser(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
