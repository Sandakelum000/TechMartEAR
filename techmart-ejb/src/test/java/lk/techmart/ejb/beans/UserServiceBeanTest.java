package lk.techmart.ejb.beans;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lk.techmart.core.dto.UserDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.entity.User;
import lk.techmart.core.util.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceBeanTest {
    @InjectMocks
    private UserServiceBean bean;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> query;

    private void injectEntityManager() throws Exception {
        Field field = UserServiceBean.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(bean, entityManager);
    }

    @Test
    void shouldFailWhenRequestIsNull() {

        ServiceResponse<UserSessionDTO> response = bean.userLogin(null);

        assertFalse(response.isSuccess());
        assertEquals("Please enter your credentials", response.getMessage());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {

        UserDTO dto = new UserDTO();
        dto.setEmail("");
        dto.setPassword("Password@123");

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertFalse(response.isSuccess());
        assertEquals("Please enter your email address.", response.getMessage());
    }

    @Test
    void shouldFailWhenPasswordIsEmpty() {

        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("");

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertFalse(response.isSuccess());
        assertEquals("Please enter your password.", response.getMessage());
    }

    @Test
    void shouldReturnAccountNotFound() throws Exception {

        injectEntityManager();

        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Password@123");

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenReturn(query);

        when(query.setParameter(eq("email"), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(Collections.emptyList());

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertFalse(response.isSuccess());
        assertEquals("Account not found, Please register first.", response.getMessage());
    }

    @Test
    void shouldReturnInvalidCredentials() throws Exception {

        injectEntityManager();

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("CorrectPassword");
        user.setActive(true);

        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("WrongPassword");

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenReturn(query);

        when(query.setParameter(eq("email"), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(user));

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertFalse(response.isSuccess());
        assertEquals("Invalid credentials, Please check again!", response.getMessage());
    }

    @Test
    void shouldReturnInactiveAccountMessage() throws Exception {

        injectEntityManager();

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Password@123");
        user.setActive(false);

        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Password@123");

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenReturn(query);

        when(query.setParameter(eq("email"), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(user));

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertFalse(response.isSuccess());
        assertEquals("Your account is under verification process", response.getMessage());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        injectEntityManager();

        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@test.com");
        user.setPassword("Password@123");
        user.setActive(true);

        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Password@123");

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenReturn(query);

        when(query.setParameter(eq("email"), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(user));

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());

        assertNotNull(response.getData());
        assertEquals(1, response.getData().getId());
        assertEquals("John", response.getData().getFirstName());
        assertEquals("Doe", response.getData().getLastName());
        assertEquals("test@test.com", response.getData().getEmail());
    }

    @Test
    void shouldHandleDatabaseException() throws Exception {

        injectEntityManager();

        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Password@123");

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenThrow(new RuntimeException("Database Error"));

        ServiceResponse<UserSessionDTO> response = bean.userLogin(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Login failed due to system error. Please try again later.",
                response.getMessage()
        );
    }

    //user register testing
    @Test
    void shouldFailWhenRegisterRequestIsNull() {

        ServiceResponse<Void> response = bean.registerUser(null);

        assertFalse(response.isSuccess());
        assertEquals("User cannot be empty", response.getMessage());
    }

    @Test
    void shouldFailWhenRegisterEmailInvalid() {

        UserDTO dto = new UserDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("abc");
        dto.setPassword("Password@123");

        ServiceResponse<Void> response = bean.registerUser(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Please add a valid email address",
                response.getMessage()
        );
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() throws Exception {

        injectEntityManager();

        UserDTO dto = new UserDTO();
        dto.setFirstName("kamal");
        dto.setLastName("perera");
        dto.setEmail("kamal@test.com");
        dto.setPassword("Password@123");

        User existing = new User();

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenReturn(query);

        when(query.setParameter(eq("email"), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(existing));

        ServiceResponse<Void> response = bean.registerUser(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "This email already exists! Please use another email",
                response.getMessage()
        );
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {

        injectEntityManager();

        UserDTO dto = new UserDTO();
        dto.setFirstName("kamal");
        dto.setLastName("sudarshana");
        dto.setEmail("kamal@test.com");
        dto.setPassword("Password@123");

        when(entityManager.createNamedQuery("User.getByEmail", User.class))
                .thenReturn(query);

        when(query.setParameter(eq("email"), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(Collections.emptyList());

        ServiceResponse<Void> response = bean.registerUser(dto);

        assertTrue(response.isSuccess());
        assertEquals("Account created successfully.", response.getMessage());

        verify(entityManager, times(1)).persist(any(User.class));
    }
}
