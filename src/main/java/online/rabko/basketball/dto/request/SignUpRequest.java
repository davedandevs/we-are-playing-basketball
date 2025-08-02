package online.rabko.basketball.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the user registration (sign-up) request data.
 */
@Data
@Builder
public class SignUpRequest {

    private String username;
    private String password;
}
