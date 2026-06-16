package _LicHub.Backend.playerService.dtos;

import java.io.Serializable;

import _LicHub.Backend.playerService.entities.Player;
import _LicHub.Backend.playerService.enums.AuthType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PlayerCache implements Serializable {

    private AuthType authType;
    private Player player;

}
