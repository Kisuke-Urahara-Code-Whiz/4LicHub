package _LicHub.Backend.playerService.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class AuthResponse {

   private boolean success;
   private Object body;

}
