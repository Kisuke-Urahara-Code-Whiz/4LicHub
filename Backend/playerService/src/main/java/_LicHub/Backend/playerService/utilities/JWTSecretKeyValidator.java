package _LicHub.Backend.playerService.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter
@Setter
public class JWTSecretKeyValidator {

    @NotBlank(message = "JWT secret key cannot be blank")
    @Size(min = 32, message = "JWT secret key must be at least 32 characters long")
    private String secret;

}