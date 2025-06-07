package com.gha.gender_healthcare_api.controller;

import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.dto.request.LoginRequest;
import com.gha.gender_healthcare_api.dto.request.RegisterRequest;
import com.gha.gender_healthcare_api.dto.response.JwtAuthenticationResponse;
import com.gha.gender_healthcare_api.service.UserService;
import com.gha.gender_healthcare_api.security.JwtTokenProvider;
import com.gha.gender_healthcare_api.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return new ResponseEntity<>("Username đã tồn tại!", HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>("Email đã tồn tại!", HttpStatus.BAD_REQUEST);
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getFullName(),
                registerRequest.getGender(),
                registerRequest.getDateOfBirth(),
                registerRequest.getEmail(),
                registerRequest.getPhoneNumber(),
                User.Role.CUSTOMER);

        userService.saveUser(user);

        return new ResponseEntity<>("Đăng ký người dùng thành công!", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                return ResponseEntity.ok("Xin chào, " + userDetails.getUsername() + "! Vai trò của bạn là: "
                        + userDetails.getRole() + ". ID: " + userDetails.getId());
            } else {
                return ResponseEntity.ok("Xin chào, " + principal.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập.");
    }
}
