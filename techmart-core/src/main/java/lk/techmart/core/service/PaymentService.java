package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.PaymentHistoryDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Remote
public interface PaymentService {

    ServiceResponse<List<PaymentHistoryDTO>> userPaymentHistory(UserSessionDTO user);
}
