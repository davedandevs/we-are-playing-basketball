package online.rabko.basketball.unit.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import online.rabko.basketball.controller.AuthController;
import online.rabko.basketball.service.AuthenticationService;
import online.rabko.model.JwtAuthenticationResponse;
import online.rabko.model.SignInRequest;
import online.rabko.model.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link AuthController} using RestAssuredMockMvc.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(authController);
    }

    @Test
    void signUp_shouldReturnToken() throws Exception {
        SignUpRequest request = new SignUpRequest("testuser", "pass123");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("mock-token");

        when(authenticationService.signUp(any())).thenReturn(response);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/auth/sign-up")
            .then()
            .statusCode(200)
            .body("token", equalTo("mock-token"));
    }

    @Test
    void signIn_shouldReturnToken() throws Exception {
        SignInRequest request = new SignInRequest("testuser", "pass123");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("token-123");

        when(authenticationService.getToken(any())).thenReturn(response);

        given()
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .when()
            .post("/auth/token")
            .then()
            .statusCode(200)
            .body("token", equalTo("token-123"));
    }
}
