package ca.n4dev.aegaeon.server.controller;

import java.util.Base64;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


/**
 * TokensControllerIntegratedTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 08 - 2018
 */
public class TokensControllerIntegratedTest extends BaseIntegratedControllerTest<TokensController> {

    private static final String CLIENT_PUBLIC_ID = "ca.n4dev.auth.client";
    private static final String CLIENT_PASSWD = "ca.n4dev.auth.client";

    @Override
    protected Class<TokensController> getControllerClass() {
        return TokensController.class;
    }

    /*
    * @RequestParam(value = "grant_type", required = false) String pGrantType,
                    @RequestParam(value = "code", required = false) String pCode,
                    @RequestParam(value = "redirect_uri", required = false) String pRedirectUri,
                    @RequestParam(value = "client_id", required = false) String pClientPublicId,
                    @RequestParam(value = "scope", required = false) String pScope,
                    @RequestParam(value = "refresh_token", required = false) String pRefreshToken,
    * */

    @Test
    public void isUnauthorized() throws Exception {
        mockMvc.perform(post(TokensController.URL))
               .andExpect(MockMvcResultMatchers
                                  .status()
                                  .is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = CLIENT_PUBLIC_ID, roles = {"CLIENT"})
    public void invalidGrantTypeParam() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "invalid");
        callAndExpect(params, HttpStatus.OK, MockMvcResultMatchers.view().name("error"));
    }

    @Test
    public void invalidCodeParam() throws Exception {
        MultiValueMap<String, String> params = getValidClient1Params();
        params.set("code", "invalid_code");
        callAndExpect(params, HttpStatus.FOUND, MockMvcResultMatchers.redirectedUrlPattern(params.getFirst("redirect_uri") + "*"));
    }

    @Test
    public void invalidRedirectionParam() throws Exception {
        MultiValueMap<String, String> params = getValidClient1Params();
        params.set("redirect_uri", "http://bad-url.tld/login");
        callAndExpect(params, HttpStatus.OK, MockMvcResultMatchers.view().name("error"));
    }

    @Test
    public void unauthorizedScopeIsIgnore() throws Exception {

        MultiValueMap<String, String> params = getValidClient1Params();
        params.set("scope", "openid profile social"); // social
        MvcResult mvcResult =
                callAndExpect(params, HttpStatus.OK, MockMvcResultMatchers.redirectedUrlPattern(params.getFirst("redirect_uri") + "*"));

        mvcResult.toString();
    }

    private MvcResult callAndExpect(MultiValueMap<String, String> pParams, HttpStatus pHttpStatus, ResultMatcher pResultMatcher)
            throws Exception {

        MvcResult result =
                mockMvc.perform(post(TokensController.URL)
                                        //.header("Authorization", "Basic " + authorizationHeader())
                                        .params(pParams))
                       .andExpect(MockMvcResultMatchers
                                          .status()
                                          .is(pHttpStatus.value()))
                       .andExpect(pResultMatcher)
                       .andReturn();

        return result;
    }


    private String authorizationHeader() {
        return Base64.getEncoder().encodeToString((CLIENT_PUBLIC_ID + ":" + CLIENT_PASSWD).getBytes());
    }

    private MultiValueMap<String, String> getValidClient1Params() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "code");
        params.add("code", "0xffA");
        params.add("redirect_uri", "http://localhost/login.html");
        params.add("client_id", CLIENT_PUBLIC_ID);
        params.add("scope", "openid profile");

        return params;
    }
}
