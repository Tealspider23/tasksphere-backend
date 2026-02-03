package com.tasksphere.service;

import com.tasksphere.dto.AuthResponse;
import com.tasksphere.dto.LoginRequest;
import com.tasksphere.dto.RegisterRequest;
import com.tasksphere.enums.Role;
import com.tasksphere.model.User;
import com.tasksphere.repository.UserRepository;
import com.tasksphere.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repo , PasswordEncoder encoder , JwtUtil jwtUtil){
        this.userRepository = repo;
        this.passwordEncoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req){
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getEmail(),user.getRole().name()));
    }

    public AuthResponse login(LoginRequest req){
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if(!passwordEncoder.matches(req.getPassword(),user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        return new AuthResponse(jwtUtil.generateToken(user.getEmail(),user.getRole().name()));
    }


}
