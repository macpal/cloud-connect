package com.cloudconnect.auth.controller;

import com.cloudconnect.auth.dto.AuthResponseDto;
import com.cloudconnect.auth.dto.LoginRequestDto;
import com.cloudconnect.auth.dto.SignUpRequestDto;
import com.cloudconnect.auth.entity.Role;
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
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword())); // Hash password

        // Convert role names to Role entities
        Set<Role> roles = new HashSet<>();
        for (String roleName : signUpRequestDto.getRoles()) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " not found."));
            roles.add(role);
        }
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        // Build extra claims (you can add roles or other details)
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginRequestDto.getUsername());

        // Generate JWT token (pass all required arguments)
        String token = jwtUtil.generateToken(loginRequestDto.getUsername(), claims, false);

        return new AuthResponseDto(token);
    }
}
