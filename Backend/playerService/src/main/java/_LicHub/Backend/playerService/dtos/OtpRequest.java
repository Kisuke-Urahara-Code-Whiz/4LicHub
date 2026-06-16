package _LicHub.Backend.playerService.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtpRequest {

    private String email;
    private String otp;
    private String authType;

}
