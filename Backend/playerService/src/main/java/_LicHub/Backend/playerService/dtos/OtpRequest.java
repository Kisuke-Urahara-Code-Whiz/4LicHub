package _LicHub.Backend.playerService.dtos;

import _LicHub.Backend.playerService.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtpRequest {

    private String email;
    private String otp;
    private AuthType authType;

}
