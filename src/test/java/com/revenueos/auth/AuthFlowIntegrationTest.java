package com.revenueos.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revenueos.permission.Permission;
import com.revenueos.permission.PermissionRepository;
import com.revenueos.permission.Permissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PermissionRepository permissionRepository;

    @BeforeEach
    void seedPermissions() {
        if (permissionRepository.count() == 0) {
            Permissions.ALL.forEach(code -> {
                Permission permission = new Permission();
                permission.setCode(code);
                permission.setCategory("General");
                permissionRepository.save(permission);
            });
        }
    }

    @Test
    void registersCompanyThenLogsInAndReadsCurrentUser() throws Exception {
        String register = """
                {"companyName":"Acme Inc","adminName":"Ada","email":"ada@acme.test","password":"Password123!"}
                """;

        MvcResult registered = mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("ada@acme.test"))
                .andExpect(jsonPath("$.user.roles[0]").value("COMPANY_ADMIN"))
                .andReturn();

        String accessToken = readToken(registered);

        // The access token from registration authenticates /me.
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@acme.test"))
                .andExpect(jsonPath("$.permissions").isArray());

        // Logging in with the same credentials returns a fresh token pair.
        String login = """
                {"email":"ada@acme.test","password":"Password123!"}
                """;
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void rejectsWrongPassword() throws Exception {
        String register = """
                {"companyName":"Globex","adminName":"Hank","email":"hank@globex.test","password":"Password123!"}
                """;
        mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated());

        String badLogin = """
                {"email":"hank@globex.test","password":"wrong-password"}
                """;
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(badLogin))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void blocksUnauthenticatedAccessToMe() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    private String readToken(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = node.get("accessToken").asText();
        assertThat(token).isNotBlank();
        return token;
    }
}
