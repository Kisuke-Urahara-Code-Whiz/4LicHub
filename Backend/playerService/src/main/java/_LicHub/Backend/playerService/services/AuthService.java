package _LicHub.Backend.playerService.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import _LicHub.Backend.playerService.dtos.AuthRequest;
import _LicHub.Backend.playerService.dtos.AuthResponse;
import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.exceptions.InvalidCredentialsException;
import _LicHub.Backend.playerService.repositories.PlayerRepository;
import _LicHub.Backend.playerService.utilities.JWTUtil;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;
    private final JWTUtil jwtUtil;

    AuthService(PasswordEncoder passwordEncoder, PlayerRepository playerRepository, JWTUtil jwtUtil){
        this.passwordEncoder = passwordEncoder;
        this.playerRepository = playerRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signUp(Player newPlayer){
        newPlayer.setPassword(passwordEncoder.encode(newPlayer.getPassword()));
        playerRepository.save(newPlayer);
        return AuthResponse.builder()
                .name(newPlayer.getUserName())
                .email(newPlayer.getEmail())
                .jwt(jwtUtil.generateToken(newPlayer))
                .role(newPlayer.getRole())
                .build();
    }

    public AuthResponse signIn(AuthRequest authRequest) {

        Player player;

        if (authRequest.getName() != null && !authRequest.getName().isBlank()) {
            player = playerRepository
                    .findByUserName(authRequest.getName())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid username/email or password"));
        } else if (authRequest.getEmail() != null && !authRequest.getEmail().isBlank()) {
            player = playerRepository
                    .findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid username/email or password"));
        } else {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }

        if (!passwordEncoder.matches( authRequest.getPassword(), player.getPassword()))
        { throw new InvalidCredentialsException("Invalid credentials"); }

        return AuthResponse.builder()
                .name(player.getUserName())
                .email(player.getEmail())
                .jwt(jwtUtil.generateToken(player))
                .role(player.getRole())
                .build();
    }

}
