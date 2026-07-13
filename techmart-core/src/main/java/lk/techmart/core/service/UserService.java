package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.PaymentHistoryDTO;
import lk.techmart.core.dto.UserDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.User;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Remote
public interface UserService {
    ServiceResponse<Void> registerUser(UserDTO userDTO);
    ServiceResponse<UserSessionDTO> userLogin(UserDTO userDTO);
}
