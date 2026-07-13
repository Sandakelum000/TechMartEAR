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
public class PaymentEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderId;
    private int statusCode;
}
