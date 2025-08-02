package online.rabko.basketball.domain.dto;

import lombok.Data;

@Data
public class SignInRequest {

    private String login;

    private String password;
}
