package _LicHub.Backend.playerService.controllers;

import com.bastiaanjansen.otp.TOTPGenerator;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import _LicHub.Backend.playerService.services.EmailService;
import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/playerService")
public class PlayerServiceController {

    EmailService emailService;
    TOTPGenerator totpGenerator;

    PlayerServiceController(EmailService emailService, TOTPGenerator totpGenerator){
        this.emailService = emailService;
        this.totpGenerator = totpGenerator;
    }

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok("Route working");
    }

    @PostMapping
    public ResponseEntity<?> post() throws MessagingException {
        emailService.sendAuthEmail("spyethanace@gmail.com", "Tanzu6912", totpGenerator.now());
        return ResponseEntity.ok("Email sent");
    }
}
