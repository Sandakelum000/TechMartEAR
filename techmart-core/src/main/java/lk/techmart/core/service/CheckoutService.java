package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.CheckoutDataDTO;
import lk.techmart.core.dto.CheckoutRequestDTO;
import lk.techmart.core.dto.PayHereDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.util.ServiceResponse;

@Remote
public interface CheckoutService {
    ServiceResponse<PayHereDTO> processCheckout(CheckoutRequestDTO requestDTO , UserSessionDTO user);
    ServiceResponse<CheckoutDataDTO> getCheckoutData(UserSessionDTO user);
}
