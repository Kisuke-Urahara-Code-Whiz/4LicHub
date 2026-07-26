package _LicHub.Backend.playerService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import _LicHub.Backend.playerService.entities.Player;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByEmail(String email);
    Optional<Player> findByUserName(String name);

    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}