package _LicHub.Backend.playerService.services;

import com.bastiaanjansen.otp.TOTPGenerator;

import org.springframework.cache.Cache;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import _LicHub.Backend.playerService.dtos.AuthRequest;
import _LicHub.Backend.playerService.dtos.AuthResponse;
import _LicHub.Backend.playerService.dtos.OtpRequest;
import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.exceptions.InvalidCredentialsException;
import _LicHub.Backend.playerService.repositories.PlayerRepository;
import _LicHub.Backend.playerService.utilities.JWTUtil;
import jakarta.mail.MessagingException;

@Service
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

        newPlayer.setPassword(passwordEncoder.encode(newPlayer.getPassword()));
        sendOTP(newPlayer);
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

        sendOTP(player);
    }

    public void sendOTP(Player player) throws MessagingException {
        String otp = totpGenerator.now();
        if(otpCache!=null) otpCache.put(player.getEmail(), otp);
        if(playerCache!=null) playerCache.put(player.getEmail(), player);
        emailService.sendAuthEmail(player.getEmail(), player.getUserName(), otp);
    }

    public void resendOtp(String email) throws MessagingException {
        if (otpCache == null || playerCache == null) return;
        String otp = otpCache.get(email, String.class);
        Player player = playerCache.get(email, Player.class);

        if (otp == null || player == null) throw new RuntimeException("Session expired, cannot resend OTP");

        emailService.sendAuthEmail(player.getEmail(), player.getUserName(), otp);
    }

    public AuthResponse validateOtp(OtpRequest otpRequest){

        if (otpCache == null || playerCache == null) return null;
        if(!Objects.equals(otpCache.get(otpRequest.getEmail(), String.class), otpRequest.getOtp())){
            throw new InvalidCredentialsException("Invalid Otp");
        }

        Player player = playerCache.get(otpRequest.getEmail(), Player.class);

        if(player==null) throw new RuntimeException("Player not found");
        if(otpRequest.getAuthType().equals("register")) playerRepository.save(player);

        otpCache.evict(otpRequest.getEmail());
        playerCache.evict(otpRequest.getEmail());

        return AuthResponse.builder()
                .name(player.getUserName())
                .email(player.getEmail())
                .jwt(jwtUtil.generateToken(player))
                .role(player.getRole())
                .build();

    }



}
