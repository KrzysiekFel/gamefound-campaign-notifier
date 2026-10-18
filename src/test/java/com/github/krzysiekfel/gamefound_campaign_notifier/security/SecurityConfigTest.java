package com.github.krzysiekfel.gamefound_campaign_notifier.security;

import com.github.krzysiekfel.gamefound_campaign_notifier.controller.AuthController;
import com.github.krzysiekfel.gamefound_campaign_notifier.controller.CampaignSearchController;
import com.github.krzysiekfel.gamefound_campaign_notifier.controller.EmailRequestController;
import com.github.krzysiekfel.gamefound_campaign_notifier.controller.GameImportController;
import com.github.krzysiekfel.gamefound_campaign_notifier.controller.UserController;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.Role;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.EmailRequestMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.UserMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.AuthService;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.CampaignSearchService;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.EmailRequestService;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.GameCsvImportService;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Web layer only (no database, Rabbit, Redis): real SecurityConfig + JwtConfig + TokenService, mocked services.
// Requests go without the /api context path – MockMvc talks to the DispatcherServlet directly.
@WebMvcTest(
        controllers = {
                AuthController.class,
                CampaignSearchController.class,
                EmailRequestController.class,
                GameImportController.class,
                UserController.class
        },
        properties = "security.jwt.secret=test-only-jwt-secret-at-least-32-bytes-long")
@Import({SecurityConfig.class, JwtConfig.class, TokenService.class})
class SecurityConfigTest {

    private static final MockMultipartFile CSV_FILE =
            new MockMultipartFile("file", "games.csv", "text/csv", "id,name\n".getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private CampaignSearchService campaignSearchService;

    @MockitoBean
    private EmailRequestService emailRequestService;

    @MockitoBean
    private EmailRequestMapper emailRequestMapper;

    @MockitoBean
    private GameCsvImportService gameCsvImportService;

    @Test
    void shouldAllowAnonymousAccessToPublicEndpoints() throws Exception {
        // GIVEN
        when(authService.login(any(), any())).thenReturn(new AccessToken("jwt-value", Instant.now()));

        // WHEN / THEN
        mockMvc.perform(get("/v1/campaigns"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test.user@example.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAnonymousAccessToProtectedEndpoints() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get("/v1/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
        mockMvc.perform(post("/v1/email-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectUrlName\":\"age-of-skorpa\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/v1/email-requests/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(multipart("/admin/games/import").file(CSV_FILE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAuthenticatedUserToAccessProtectedEndpoints() throws Exception {
        // GIVEN
        String token = tokenFor(Role.USER);

        // WHEN / THEN
        mockMvc.perform(get("/v1/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
        // passes security; the (mocked) service finds nothing → 404 from the controller, not 401/403
        mockMvc.perform(get("/v1/email-requests/{id}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get("/v1/me").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidTokenEvenOnPublicEndpoint() throws Exception {
        // The frontend must drop a stale token, otherwise even the public search returns 401
        mockMvc.perform(get("/v1/campaigns").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldForbidAdminEndpointForRegularUser() throws Exception {
        // WHEN / THEN
        mockMvc.perform(multipart("/admin/games/import").file(CSV_FILE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFor(Role.USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminEndpointForAdmin() throws Exception {
        // WHEN / THEN
        mockMvc.perform(multipart("/admin/games/import").file(CSV_FILE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFor(Role.ADMIN)))
                .andExpect(status().isNoContent());
    }

    private String tokenFor(Role role) {
        User user = new User("krzysiek", "test.user@example.com", "$2a$10$hash");
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", role);
        return tokenService.generate(user).value();
    }
}
