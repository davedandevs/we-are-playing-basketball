package online.rabko.basketball.dto.request;

import lombok.Builder;
import lombok.Data;

/**
 * DTO representing the user credentials for authentication (sign-in).
 */
@Data
@Builder
public class SignInRequest {

    private String username;
    private String password;
}
