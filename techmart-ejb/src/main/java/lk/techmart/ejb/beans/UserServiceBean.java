package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.PaymentHistoryDTO;
import lk.techmart.core.dto.UserDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.Order;
import lk.techmart.core.entity.OrderItem;
import lk.techmart.core.entity.Status;
import lk.techmart.core.entity.User;
import lk.techmart.core.service.UserService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.ejb.validation.UserValidator;

import java.util.ArrayList;
import java.util.List;


@Stateless
public class UserServiceBean implements UserService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Override
    public ServiceResponse<Void> registerUser(UserDTO userDTO) {
        boolean status = false;
        String message;
        try {
            String result = UserValidator.validateRegister(userDTO);
            if (!"success".equals(result)) {
                return ServiceResponse.<Void>builder().success(status).message(result).build();
            }
            boolean isExist = !entityManager.createNamedQuery("User.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getResultList()
                    .isEmpty();

            if (isExist) {
                message = "This email already exists! Please use another email";
                return ServiceResponse.<Void>builder().success(status).message(message).build();
            }

            User user = new User();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setActive(true);

            entityManager.persist(user);
            status = true;
            message = "Account created successfully.";
            return ServiceResponse.<Void>builder().success(status).message(message).build();

        } catch (Exception e) {
            e.printStackTrace();
            message = "User account creation failed due to system error.Please try again later";
            return ServiceResponse.<Void>builder().success(status).message(message).build();
        }

    }

    @Override
    public ServiceResponse<UserSessionDTO> userLogin(UserDTO userDTO) {
        boolean status = false;
        try {
            String result = UserValidator.validateLogin(userDTO);
            if (!"success".equals(result)) {
                return ServiceResponse.<UserSessionDTO>builder().success(status).message(result).build();
            }
            User user = entityManager.createNamedQuery("User.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                return ServiceResponse.<UserSessionDTO>builder().success(status).message("Account not found, Please register first.").build();
            }
            if (!user.getPassword().equals(userDTO.getPassword())) {
                return ServiceResponse.<UserSessionDTO>builder().success(status).message("Invalid credentials, Please check again!").build();
            }
            if (!user.isActive()) {
                return ServiceResponse.<UserSessionDTO>builder().success(status).message("Your account is under verification process").build();
            }
            status = true;
            return ServiceResponse.<UserSessionDTO>builder()
                    .success(status)
                    .message("Login successful")
                    .data(new UserSessionDTO(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<UserSessionDTO>builder().success(status).message("Login failed due to system error. Please try again later.").build();
        }
    }

}
