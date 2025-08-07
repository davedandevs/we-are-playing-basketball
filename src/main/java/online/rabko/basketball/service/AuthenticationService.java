package online.rabko.basketball.service;

import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.exception.UserAlreadyExistsException;
import online.rabko.model.JwtAuthenticationResponse;
import online.rabko.model.Role;
import online.rabko.model.SignInRequest;
import online.rabko.model.SignUpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling user registration and authentication, including JWT token
 * generation.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user, encodes their password, assigns the default role, and returns a JWT
     * token.
     *
     * @param request the sign-up request containing username and password
     * @return the JWT authentication response with generated token
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(request.getUsername());
        }
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();

        userService.create(user);

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }


    /**
     * Generates a JWT access token for a valid user. Authenticates the user using provided
     * credentials and returns a JWT if authentication is successful.
     *
     * @param request the token request containing username and password
     * @return the JWT authentication response with the generated token
     */
    public JwtAuthenticationResponse getToken(SignInRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        UserDetails userDetails = userService
            .userDetailsService()
            .loadUserByUsername(request.getUsername());

        String jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
