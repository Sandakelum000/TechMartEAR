package lk.techmart.core.util;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
