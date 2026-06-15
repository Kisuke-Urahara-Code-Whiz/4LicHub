package _LicHub.Backend.playerService.utilities;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EmailValidationUtil {

    private EmailValidator validator;

    @PostConstruct
    public void init(){
        validator = EmailValidator.getInstance();
    }

    public boolean checkValidation(String email){
        return validator.isValid(email);
    }

}
