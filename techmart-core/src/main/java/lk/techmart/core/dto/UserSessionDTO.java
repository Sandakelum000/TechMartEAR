package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSessionDTO implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
}
