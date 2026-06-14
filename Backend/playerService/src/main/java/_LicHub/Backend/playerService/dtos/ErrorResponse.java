package _LicHub.Backend.playerService.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String error;

}
