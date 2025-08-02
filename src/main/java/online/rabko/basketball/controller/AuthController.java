package online.rabko.basketball.controller;

import lombok.RequiredArgsConstructor;
import online.rabko.api.AuthApi;
import online.rabko.basketball.service.AuthenticationService;
import online.rabko.model.JwtAuthenticationResponse;
import online.rabko.model.SignInRequest;
import online.rabko.model.SignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller that handles user authentication operations.
 */
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthenticationService authenticationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<JwtAuthenticationResponse> authSignInPost(SignInRequest signInRequest) {
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<JwtAuthenticationResponse> authSignUpPost(SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.signUp(signUpRequest));
    }
}

