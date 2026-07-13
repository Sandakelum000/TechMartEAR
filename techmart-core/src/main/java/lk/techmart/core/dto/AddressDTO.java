package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO implements Serializable {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private boolean primary;
    private String lineOne;
    private String lineTwo;
    private String postalCode;
    private String mobile;
}
