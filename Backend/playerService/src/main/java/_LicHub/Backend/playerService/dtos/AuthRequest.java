package _LicHub.Backend.playerService.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthRequest {

    private String name;
    private String password = null ;
    private String email = null ;

}
