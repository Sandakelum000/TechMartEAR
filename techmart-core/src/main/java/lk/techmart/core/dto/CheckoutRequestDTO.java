package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDTO implements Serializable {
    private boolean isCurrentAddress;
    private String firstName;
    private String lastName;
    private int cityId;
    private String lineOne;
    private String lineTwo;
    private String postalCode;
    private String mobile;
}
