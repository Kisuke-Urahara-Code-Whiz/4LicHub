package _LicHub.Backend.playerService.services;

import org.springframework.stereotype.Service;

import _LicHub.Backend.playerService.dtos.AuthResponse;
import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.repositories.PlayerRepository;

@Service
public class AuthService {

    private final PlayerRepository playerRepository;

    AuthService(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }

    public AuthResponse signUp(Player newPlayer){
        System.out.println(newPlayer.toString());
        playerRepository.save(newPlayer);
        return AuthResponse.builder()
                .name(newPlayer.getUserName())
                .email(newPlayer.getEmail())
                .jwt("Custom JWT")
                .role(newPlayer.getRole())
                .build();
    }

}
