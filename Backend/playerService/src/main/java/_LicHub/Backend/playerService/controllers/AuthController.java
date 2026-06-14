package _LicHub.Backend.playerService.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import _LicHub.Backend.playerService.dtos.AuthRequest;
import _LicHub.Backend.playerService.dtos.AuthResponse;
import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder){
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody AuthRequest authRequest){
        System.out.println(authRequest.toString());
        AuthResponse response = authService.signUp(
                Player.builder()
                        .email(authRequest.getEmail())
                        .userName(authRequest.getName())
                        .password(passwordEncoder.encode(authRequest.getPassword()))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}