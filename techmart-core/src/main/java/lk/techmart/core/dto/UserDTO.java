package lk.techmart.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserDTO implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String lineOne;
    private String lineTwo;
    private String postalCode;
    private String mobile;
    private String newPassword;
    private String confirmPassword;
}
