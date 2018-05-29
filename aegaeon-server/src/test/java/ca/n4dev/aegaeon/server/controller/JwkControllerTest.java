package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JwkControllerTest.java
 * <p>
 * Test PublicJwkController.
 *
 * @author rguillemette
 * @since 2.0.0 - May 25 - 2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class JwkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeysProvider keysProvider;

    @Test
    public void getPublicJwks() throws Exception {

        JWKSet jwkSet = keysProvider.getJwkSet();

        RSAKey jwk = (RSAKey) jwkSet.getKeys().get(0);

        this.mockMvc.perform(get(PublicJwkController.URL))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.keys", notNullValue()))
                    .andExpect(jsonPath("$.keys[0]", notNullValue()))
                    .andExpect(jsonPath("$.keys[0].kty", is(jwk.getKeyID())))
                    .andExpect(jsonPath("$.keys[0].kid", is(jwk.getKeyType().getValue())))
                    .andExpect(jsonPath("$.keys[0].e", is(jwk.getPublicExponent().toString())))
                    .andExpect(jsonPath("$.keys[0].n", is(jwk.getModulus().toString())))
                    .andExpect(jsonPath("$.keys.length()", is(1)));

    }
}
