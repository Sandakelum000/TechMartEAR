package lk.techmart.ejb.validation;

import lk.techmart.core.dto.UserDTO;

public class UserValidator {
    public static String validateRegister(UserDTO userDTO){
        if (userDTO == null) {
            return "User cannot be empty";
        }
        if (userDTO.getFirstName() == null || userDTO.getFirstName().isBlank()) {
            return "First Name is required";
        }
        if (userDTO.getLastName() == null || userDTO.getLastName().isBlank()) {
            return "Last Name is required";
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            return "Email is required";
        }
        if (!userDTO.getEmail().matches(lk.techmart.core.util.Validator.EMAIL_VALIDATION)) {
            return "Please add a valid email address";
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return "Password is required";
        }
        if (!userDTO.getPassword().matches(lk.techmart.core.util.Validator.PASSWORD_VALIDATION)) {
            return "Password must be at least 8 characters, include uppercase, lowercase, digit, and special character";
        }
        return "success";
    }

    public static String validateLogin(UserDTO userDTO){
        if (userDTO == null) {
            return "Please enter your credentials";
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            return "Please enter your email address.";
        }
        if (!userDTO.getEmail().matches(lk.techmart.core.util.Validator.EMAIL_VALIDATION)) {
            return "Please add a valid email address.";
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return "Please enter your password.";
        }
        return "success";
    }
}
