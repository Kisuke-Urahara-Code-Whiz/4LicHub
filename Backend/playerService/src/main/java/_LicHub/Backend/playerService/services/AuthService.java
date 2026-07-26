package _LicHub.Backend.playerService.services;

import com.bastiaanjansen.otp.TOTPGenerator;
import org.springframework.cache.Cache;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import _LicHub.Backend.playerService.dtos.*;
import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.enums.AuthType;
import _LicHub.Backend.playerService.exceptions.InvalidCredentialsException;
import _LicHub.Backend.playerService.repositories.PlayerRepository;
import _LicHub.Backend.playerService.utilities.JWTUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;
    private final EmailService emailService;
    private final JWTUtil jwtUtil;
    private final TOTPGenerator totpGenerator;
    private final Cache otpCache;
    private final Cache playerCache;

    AuthService(PasswordEncoder passwordEncoder, PlayerRepository playerRepository, RedisCacheManager redisCacheManager, EmailService emailService, JWTUtil jwtUtil, TOTPGenerator generator){
        this.passwordEncoder = passwordEncoder;
        this.playerRepository = playerRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.totpGenerator = generator;
        this.otpCache = redisCacheManager.getCache("otps");
        this.playerCache = redisCacheManager.getCache("tempAuthRequests");
    }

    public void register(Player newPlayer) throws MessagingException {
        if (playerRepository.existsByUserName(newPlayer.getUserName()) ||
                playerRepository.existsByEmail(newPlayer.getEmail())) {
            throw new DataIntegrityViolationException("Username or Email already exists");
        }

        log.info("In register -> ");
        log.info(newPlayer.toString());
        newPlayer.setPassword(passwordEncoder.encode(newPlayer.getPassword()));
        sendOTP(PlayerCache.builder()
                .authType(AuthType.REGISTER)
                .player(newPlayer)
                .build());
    }

    public void login(AuthRequest authRequest) throws MessagingException {

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

        sendOTP(PlayerCache.builder()
                .authType(AuthType.LOGIN)
                .player(player)
                .build());
    }

    public void sendOTP(PlayerCache player) throws MessagingException {
        String otp = totpGenerator.now();
        if(otpCache!=null) otpCache.put(player.getPlayer().getEmail(), otp);
        if(playerCache!=null) playerCache.put(player.getPlayer().getEmail(), player);
        log.info("In send -> ");
        log.info(otp);
        emailService.sendAuthEmail(player.getPlayer().getEmail(), player.getPlayer().getUserName(), otp);
    }

    public void resendOtp(String email) throws MessagingException {
        if (otpCache == null || playerCache == null) throw new IllegalStateException("Redis cache unavailable");

        PlayerCache player = playerCache.get(email, PlayerCache.class);
        if (player == null) throw new RuntimeException("Player unavailable in Redis cache");

        String otp = totpGenerator.now();
        otpCache.put(email, otp);

        emailService.sendAuthEmail(email, player.getPlayer().getUserName(), otp);
    }

    public AuthResponse validateOtp(OtpRequest otpRequest) {

        log.info("In validate -> ");
        if (otpCache == null || playerCache == null) throw new IllegalStateException("Redis cache unavailable");

        String savedOtp = otpCache.get(otpRequest.getEmail(), String.class);
        String typedOtp = otpRequest.getOtp();
        log.info("Actual Otp : {}", savedOtp);
        log.info("Typed Otp : {}", typedOtp);

        if(savedOtp==null) throw new RuntimeException("Session expired, opt for a new otp");
        if(!savedOtp.equals(typedOtp)) throw new InvalidCredentialsException("Invalid Otp");

        PlayerCache player = playerCache.get(otpRequest.getEmail(), PlayerCache.class);

        if(player==null) throw new RuntimeException("Player unavailable in Redis cache");
        if(!player.getAuthType().equals(otpRequest.getAuthType())) throw new RuntimeException("Auth Type Mismatch");

        Player response = player.getPlayer();
        log.info("Saved Player object ->  : {}", player.toString());
        if(otpRequest.getAuthType().equals(AuthType.REGISTER)) playerRepository.save(player.getPlayer());

        log.info("In validate otp -> Reached here it means its true -> ");
        log.info("Details added in database");

        otpCache.evict(otpRequest.getEmail());
        playerCache.evict(otpRequest.getEmail());
        log.info("Cache Evicted");

        return AuthResponse.builder()
                .success(true)
                .body(PlayerSessionResponse.builder()
                        .name(response.getUserName())
                        .email(response.getEmail())
                        .jwt(jwtUtil.generateToken(response))
                        .role(response.getRole())
                        .build()).build();

    }



}
