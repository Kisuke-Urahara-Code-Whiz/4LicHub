package _LicHub.Backend.playerService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import _LicHub.Backend.playerService.services.EmailService;
import _LicHub.Backend.playerService.services.TestService;


@Controller
@RequestMapping("/playerService")
public class PlayerServiceController {

    EmailService emailService;
    TestService testService;

    PlayerServiceController(EmailService emailService, TestService testService){
        this.emailService = emailService;
        this.testService = testService;
    }

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok("Route working");
    }

    @PostMapping
    public ResponseEntity<?> post() throws InterruptedException {
        testService.generateOtp();
        return ResponseEntity.ok("Check logs");
    }
}
