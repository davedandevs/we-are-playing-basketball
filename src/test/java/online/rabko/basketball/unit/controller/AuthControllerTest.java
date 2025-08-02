package online.rabko.basketball.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.rabko.basketball.config.JwtAuthenticationFilter;
import online.rabko.basketball.controller.AuthController;
import online.rabko.basketball.dto.request.SignInRequest;
import online.rabko.basketball.dto.request.SignUpRequest;
import online.rabko.basketball.dto.response.JwtAuthenticationResponse;
import online.rabko.basketball.service.AuthenticationService;
import online.rabko.basketball.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for the {@link AuthController}.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signUp_shouldReturnToken() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
            .username("testuser")
            .password("pass123")
            .build();
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("mock-token");
        when(authenticationService.signUp(any())).thenReturn(response);
        mockMvc.perform(post("/auth/sign-up")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    void signIn_shouldReturnToken() throws Exception {
        SignInRequest request = SignInRequest.builder()
            .username("testuser")
            .password("pass123")
            .build();
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("token-123");
        when(authenticationService.signIn(any())).thenReturn(response);
        mockMvc.perform(post("/auth/sign-in")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("token-123"));
    }
}
