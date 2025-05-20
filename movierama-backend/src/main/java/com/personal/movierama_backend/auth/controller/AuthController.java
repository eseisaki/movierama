package com.personal.movierama_backend.auth.controller;


import com.personal.movierama_backend.auth.dto.SignupRequest;
import com.personal.movierama_backend.auth.dto.SignupResponse;
import com.personal.movierama_backend.auth.serivce.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @NotNull @RequestBody SignupRequest request) {
        String token = authService.registerUser(request);
        return ResponseEntity.ok(new SignupResponse(token));
    }

}
