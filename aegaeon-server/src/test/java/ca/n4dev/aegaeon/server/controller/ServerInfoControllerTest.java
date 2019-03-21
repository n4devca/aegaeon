package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.server.config.ServerInfo;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringEndsWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ServerInfoControllerTest.java
 * <p>
 * Test ServerInfoController.
 *
 * @author rguillemette
 * @since 2.0.0 - May 28 - 2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class ServerInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServerInfo serverInfo;

    @Test // ok
    public void getConfigurationTest() throws Exception {

        this.mockMvc.perform(get(ServerInfoController.URL))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.issuer", is(serverInfo.getIssuer())))
                    .andExpect(jsonPath("$.authorization_endpoint", is(new StringEndsWith(AuthorizationController.URL))))
                    .andExpect(jsonPath("$.token_endpoint", is(new StringEndsWith(TokensController.URL))))
                    .andExpect(jsonPath("$.userinfo_endpoint", is(new StringEndsWith(UserInfoController.URL))))
                    .andExpect(jsonPath("$.display_values_supported", notNullValue()))
                    .andExpect(jsonPath("$.scopes_supported", items("openid", "profile", "offline_access")))
                    .andExpect(jsonPath("$.subject_types_supported", items("public")))
                    .andExpect(jsonPath("$.userinfo_signing_alg_values_supported",
                                        items("Simple UUID (no signature)", "RS512", "RS256", "HS512", "HS256")))
                    .andExpect(jsonPath("$.id_token_signing_alg_values_supported",
                                        items("Simple UUID (no signature)", "RS512", "RS256", "HS512", "HS256")))
                    .andExpect(jsonPath("$.claim_types_supported", items("normal", "distributed")))
                    .andExpect(jsonPath("$.claims_parameter_supported", is(false)))
                    .andExpect(jsonPath("$.ui_locales_supported", items("fr_CA", "en")))
                    .andExpect(jsonPath("$.token_endpoint_auth_methods_supported", items("client_secret_basic")))
                    .andExpect(jsonPath("$.response_types_supported", items("code",
                                                                            "id_token token",
                                                                            "id_token")));

    }

    private Matcher items(String... pValues) {
        return containsInAnyOrder(asArray(pValues));
    }

    private String[] asArray(String... pValues) {
        return pValues;
    }
}
