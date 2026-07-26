package _LicHub.Backend.playerService.configs;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTPGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class AppConfig {

    @Value("${otp.secret}")
    private String otpSecret;

    @Bean
    public TOTPGenerator createOTPGenerator(){
        return new TOTPGenerator.Builder(otpSecret.getBytes(StandardCharsets.UTF_8))
                .withHOTPGenerator(builder -> {
                    builder.withPasswordLength(6);
                    builder.withAlgorithm(HMACAlgorithm.SHA256);
                })
                .withPeriod(Duration.ofSeconds(1))
                .build();
    }

}
