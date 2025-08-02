package online.rabko.basketball.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.dto.request.SignInRequest;
import online.rabko.basketball.dto.request.SignUpRequest;
import online.rabko.basketball.dto.response.JwtAuthenticationResponse;
import online.rabko.basketball.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller that handles user authentication operations such as registration and login. All
 * endpoints are prefixed with {@code /auth}.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user with the given credentials and returns a JWT token on success.
     *
     * @param request the user sign-up request containing email, password, etc.
     * @return a JWT authentication response containing the generated token
     */
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    /**
     * Authenticates a user using provided credentials and returns a JWT token if valid.
     *
     * @param request the user sign-in request with email and password
     * @return a JWT authentication response containing the access token
     */
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
}

