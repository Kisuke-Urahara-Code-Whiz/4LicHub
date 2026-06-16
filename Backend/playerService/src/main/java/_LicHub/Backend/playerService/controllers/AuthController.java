package _LicHub.Backend.playerService.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import _LicHub.Backend.playerService.dtos.OtpRequest;
import _LicHub.Backend.playerService.enums.AuthType;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import _LicHub.Backend.playerService.dtos.AuthRequest;
import _LicHub.Backend.playerService.dtos.AuthResponse;
import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthRequest authRequest) throws MessagingException {
        authService.register(Player.builder()
                .email(authRequest.getEmail())
                .userName(authRequest.getName())
                .password(authRequest.getPassword())
                .build()
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Otp Sent");

    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@Valid @RequestBody AuthRequest authRequest) throws MessagingException {
        authService.login(authRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Otp Sent");
    }

    @PostMapping("/resendOtp")
    public ResponseEntity<?> resendOTP(@Valid @RequestBody String email) throws MessagingException {
        authService.resendOtp(email);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Otp Sent");
    }

    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validate(@RequestBody OtpRequest otpRequest){
        AuthResponse authResponse = authService.validateOtp(otpRequest);
        if(otpRequest.getAuthType().equals(AuthType.REGISTER))
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

}