package _LicHub.Backend.playerService.services;

import com.bastiaanjansen.otp.TOTPGenerator;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestService {

    TOTPGenerator totpGenerator;

    TestService(TOTPGenerator totpGenerator){
        this.totpGenerator = totpGenerator;
    }

    public void generateOtp() throws InterruptedException {
        for (int i = 0; i <= 10; i++) {
            log.info("Otp no. {} -> {}", i, totpGenerator.now());
            Thread.sleep(1000);
        }
    }

}
