package _LicHub.Backend.playerService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import _LicHub.Backend.playerService.utilities.EmailUtil;

@Controller
@RequestMapping("/playerService")
public class PlayerServiceController {

    EmailUtil emailUtil;

    PlayerServiceController(EmailUtil emailUtil){
        this.emailUtil = emailUtil;
    }

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok("Route working");
    }

    @PostMapping
    public ResponseEntity<?> post(){
        emailUtil.sendTextEmail("spyethanace@gmail.com", "Hello", "Email is working");
        return ResponseEntity.ok("Email sent");
    }
}
