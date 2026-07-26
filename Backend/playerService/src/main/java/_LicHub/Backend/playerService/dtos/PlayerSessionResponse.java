package _LicHub.Backend.playerService.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PlayerSessionResponse {

    private String name;
    private String jwt;
    private String email;
    private String role;

}
