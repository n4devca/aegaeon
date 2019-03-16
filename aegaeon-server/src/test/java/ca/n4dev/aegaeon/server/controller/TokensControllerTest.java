package ca.n4dev.aegaeon.server.controller;

import java.util.Base64;

import ca.n4dev.aegaeon.api.protocol.GrantType;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * TokensControllerIntegratedTest.java
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 08 - 2018
 */
public class TokensControllerTest extends BaseIntegratedControllerTest<TokensController> {

    private static final String CLIENT_PUBLIC_ID = "ca.n4dev.auth.client";
    private static final String CLIENT_PASSWD = "kjaskas8993jnskajksj";

    private static final String AUTH_CODE_A = "0xffA";
    private static final String AUTH_CODE_B = "0xffB";


    @Override
    protected Class<TokensController> getControllerClass() {
        return TokensController.class;
    }

    @Test
    public void shouldNotBeAuthorizedToAccessTokenEndpoint() throws Exception {
        mockMvc.perform(post(TokensController.URL))
               .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }


    @Test
    @WithMockUser(username = CLIENT_PUBLIC_ID, roles = {"CLIENT"})
    public void shouldGetInvalidGrantErrorBecauseInvalidGrant() throws Exception {

        mockMvc.perform(
                post(TokensController.URL)
                        .param("code", AUTH_CODE_A)
                        .param("grant_type", "invalid")
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("redirect_uri", "http://localhost/login.html"))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error", is("invalid_grant")));
    }

    @Test
    public void shouldGetInvalidGrantErrorBecauseInvalidRequestMethod() throws Exception {

        String auth = "Basic " + authorizationHeader();

        mockMvc.perform(put(TokensController.URL)
                                .param("code", AUTH_CODE_A)
                                .param("grant_type", GrantType.AUTHORIZATION_CODE.toString().toLowerCase())
                                .param("client_id", CLIENT_PUBLIC_ID)
                                .param("redirect_uri", "http://localhost/login.html")
                                .header("Authorization", auth))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error", is("invalid_request")));

    }


    @Test
    public void successGetAccessToken() throws Exception {

        String auth = "Basic " + authorizationHeader();

        this.mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", AUTH_CODE_A)
                        .param("grant_type", GrantType.AUTHORIZATION_CODE.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("redirect_uri", "http://localhost/login.html")
                        .header("Authorization", auth))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token", notNullValue()))
                    .andExpect(jsonPath("$.id_token", notNullValue()))
                    .andExpect(jsonPath("$.token_type", is("Bearer")))
                    .andExpect(jsonPath("$.expires_in", greaterThan(60)));
    }

    @Test
    public void shouldGetInvalidAuthCodeError() throws Exception {

        String auth = "Basic " + authorizationHeader();

        mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", "invalid_code")
                        .param("grant_type", GrantType.AUTHORIZATION_CODE.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("redirect_uri", "http://localhost/login.html")
                        .header("Authorization", auth))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.error", is("invalid_grant")));

    }

    @Test
    public void shouldGetInvalidRedirectionUriError() throws Exception {
        String auth = "Basic " + authorizationHeader();

        mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", AUTH_CODE_A)
                        .param("grant_type", GrantType.AUTHORIZATION_CODE.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("redirect_uri", "http://bad-url.tld/login")
                        .header("Authorization", auth))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(view().name("error"));
    }

    @Test
    public void shouldGetInvalidRequestBecauseOfScopeParams() throws Exception {

        String auth = "Basic " + authorizationHeader();

        this.mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", AUTH_CODE_A)
                        .param("grant_type", GrantType.AUTHORIZATION_CODE.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("redirect_uri", "http://localhost/login.html")
                        .param("scope", "openid profile offline_access")
                        .header("Authorization", auth))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error", is("invalid_request")));

    }


    @Test
    public void successGetAccessTokenFromRefresh() throws Exception {

        String auth = "Basic " + authorizationHeader();

        this.mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("refresh_token", "9b65047c-93ce-4934-beb5-9e3239c2981b")
                        .param("grant_type", GrantType.REFRESH_TOKEN.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("scope", "openid profile offline_access")
                        .param("redirect_uri", "http://localhost/login.html")
                        .header("Authorization", auth))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token", notNullValue()))
                    .andExpect(jsonPath("$.token_type", is("Bearer")))
                    .andExpect(jsonPath("$.expires_in", greaterThan(60)));


    }


    private String authorizationHeader() {
        return Base64.getEncoder().encodeToString((CLIENT_PUBLIC_ID + ":" + CLIENT_PASSWD).getBytes());
    }

}
