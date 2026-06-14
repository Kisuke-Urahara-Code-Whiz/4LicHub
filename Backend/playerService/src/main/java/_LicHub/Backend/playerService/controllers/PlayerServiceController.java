package _LicHub.Backend.playerService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/playerService")
public class PlayerServiceController {

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok("Route working");
    }
}
