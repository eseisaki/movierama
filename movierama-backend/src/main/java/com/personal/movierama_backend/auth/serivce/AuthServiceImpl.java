package com.personal.movierama_backend.auth.serivce;

import com.personal.movierama_backend.auth.dto.SignupRequest;
import com.personal.movierama_backend.common.exception.UserAlreadyExistsException;
import com.personal.movierama_backend.common.model.User;
import com.personal.movierama_backend.common.repository.UserRepository;
import com.personal.movierama_backend.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

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
}
