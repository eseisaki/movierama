package com.personal.movierama_backend.auth.serivce;

import com.personal.movierama_backend.auth.dto.LoginRequest;
import com.personal.movierama_backend.auth.dto.SignupRequest;
import com.personal.movierama_backend.common.exception.UserAlreadyExistsException;
import com.personal.movierama_backend.common.model.User;
import com.personal.movierama_backend.common.repository.UserRepository;
import com.personal.movierama_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String registerUser(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw UserAlreadyExistsException.forUsername(request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw UserAlreadyExistsException.forEmail(request.email());
        }

        String password = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.email(), password);
        userRepository.save(user);

        return jwtUtil.generateToken(user);
    }

    @Override
    public String authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        User user = (User) authentication.getPrincipal();
        return jwtUtil.generateToken(user);
    }
}
