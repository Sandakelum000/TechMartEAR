package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayHereDTO implements Serializable {
    private String merchant_id;
    private String return_url;
    private String cancel_url;
    private String notify_url;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String order_id;
    private String items;
    private String currency;
    private String amount;
    private String hash;
    private boolean sandbox;
}
