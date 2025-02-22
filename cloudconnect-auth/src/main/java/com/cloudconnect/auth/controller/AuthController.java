package com.cloudconnect.auth.controller;

import com.cloudconnect.auth.dto.AuthResponseDto;
import com.cloudconnect.auth.dto.LoginRequestDto;
import com.cloudconnect.auth.dto.SignUpRequestDto;
import com.cloudconnect.auth.entity.Role;
import com.cloudconnect.auth.entity.RoleName;
import com.cloudconnect.auth.entity.User;
import com.cloudconnect.auth.repository.RoleRepository;
import com.cloudconnect.auth.repository.UserRepository;
import com.cloudconnect.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        Optional<User> existingUser = userRepository.findByUsername(signUpRequestDto.getUsername());
        if (existingUser.isPresent()) {
            return "Username is already taken";
        }

        User user = new User();
        user.setUsername(signUpRequestDto.getUsername());
        user.setEmail(signUpRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword())); // Hash password

        Set<Role> userRoles = signUpRequestDto.getRoles().stream()
                .map(role -> roleRepository.findByRoleName(role)
                        .orElseThrow(() -> new RuntimeException("Error: Role " + role + " not found!")))
                .collect(Collectors.toSet());

        user.setRoles(userRoles);
        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        // Get user details
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build extra claims (adding roles)
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getRoleName().name()) // Convert Enum to String
                .collect(Collectors.toList()));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), claims, false);

        return new AuthResponseDto(token);
    }

}
