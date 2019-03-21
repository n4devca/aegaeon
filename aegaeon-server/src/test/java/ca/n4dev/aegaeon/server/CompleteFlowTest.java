package ca.n4dev.aegaeon.server;

import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.api.token.OAuthUserAndClaim;
import ca.n4dev.aegaeon.server.controller.AuthorizationController;
import ca.n4dev.aegaeon.server.controller.TokensController;
import ca.n4dev.aegaeon.server.controller.UserInfoController;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.token.verifier.RSA512JwtTokenVerifier;
import ca.n4dev.aegaeon.server.utils.RequestBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CompleteFlowTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 18 - 2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class CompleteFlowTest {

    private static final String USER_NAME = "user@localhost";
    private static final String USER_PASSWD = "user@localhost";
    private static final String CLIENT_PUBLIC_ID = "ca.n4dev.auth.client";
    private static final String CLIENT_REDIRECTION = "http://localhost/login.html";
    private static final String CLIENT_PASSWD = "kjaskas8993jnskajksj";
    private static final String STATE = "state1";
    private static final String NONCE = "nonce1";
    private static final String SCOPE = "openid profile";
    private static final String DISPLAY = "desktop";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RSA512JwtTokenVerifier rsa512JwtTokenVerifier;

    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * The goal of this test is to mimic what a client using the authorization_code flow would do :
     * 1. Get an authorization code from /authorize
     * 2. Challenge this code on /token
     * 3. Get user information from /userinfo
     */
    @Test
    public void successGetAuthCodeThenAccessTokenThenUserInfo() throws Exception {

        // 1. /authorize
        final MvcResult authCodeResult = getAuthCode(false);
        final String redirectedUrl = authCodeResult.getResponse().getRedirectedUrl();
        Assert.assertTrue(Utils.isNotEmpty(redirectedUrl));

        // Check Params
        final Map<String, String> parameters = extractParameters(redirectedUrl);
        Assert.assertNotNull(parameters.get("state"));
        Assert.assertNotNull(parameters.get("code"));

        // 2. /token
        final MvcResult tokenResult = getTokenWithCode(parameters.get("code"), CLIENT_PUBLIC_ID, CLIENT_PASSWD);
        final String contentAsString = tokenResult.getResponse().getContentAsString();
        Assert.assertTrue(Utils.isNotEmpty(contentAsString));

        final Map<String, Object> tokenResponse = objectMapper.readValue(contentAsString, Map.class);
        Assert.assertNotNull(tokenResponse);
        checkToken(tokenResponse, false);

        final OAuthUserAndClaim idTokenUser = validateToken((String) tokenResponse.get("id_token"));
        final OAuthUserAndClaim accessTokenUser = validateToken((String) tokenResponse.get("access_token"));

        // Validate info in token
        Assert.assertNotNull(idTokenUser.getOAuthUser());
        Assert.assertNotNull(accessTokenUser.getOAuthUser());
        Assert.assertTrue(Utils.isNotEmpty(idTokenUser.getOAuthUser().getUniqueIdentifier()));
        Assert.assertTrue(Utils.isNotEmpty(accessTokenUser.getOAuthUser().getUniqueIdentifier()));

        // 3. /userinfo
        final MvcResult userInfoResponse = getUserInfo((String) tokenResponse.get("access_token"));
        final String userInfoJsonString = userInfoResponse.getResponse().getContentAsString();
        Assert.assertTrue(Utils.isNotEmpty(userInfoJsonString));

        final Map<String, Object> user = objectMapper.readValue(userInfoJsonString, Map.class);
        Assert.assertNotNull(user);
        Assert.assertTrue(Utils.isNotEmpty((String) user.get("sub")));
        Assert.assertTrue(Utils.isNotEmpty((String) user.get("preferred_username")));
    }

    /**
     * The goal of this test is to mimic what a client using the implicit flow would do :
     * 1. Get an access token from /authorize
     * 2. Get user information from /userinfo
     */
    @Test
    public void successGetImplicitThenUserInfo() throws Exception {

        final MvcResult tokenUsingImplicit = getTokenUsingImplicit();
        final String redirectedUrl = tokenUsingImplicit.getResponse().getRedirectedUrl();
        Assert.assertTrue(Utils.isNotEmpty(redirectedUrl));
        Assert.assertTrue(redirectedUrl.startsWith(CLIENT_REDIRECTION + "#"));

        // Check Params
        final Map<String, String> parameters = extractParameters(redirectedUrl);
        Assert.assertTrue(Utils.isNotEmpty(parameters.get("access_token")));
        Assert.assertTrue(Utils.isNotEmpty(parameters.get("id_token")));
        Assert.assertNull(parameters.get("refresh_token"));

        final OAuthUserAndClaim idTokenUser = validateToken((String) parameters.get("id_token"));
        final OAuthUserAndClaim accessTokenUser = validateToken((String) parameters.get("access_token"));

        // Validate info
        Assert.assertNotNull(idTokenUser.getOAuthUser());
        Assert.assertNotNull(accessTokenUser.getOAuthUser());
        Assert.assertTrue(Utils.isNotEmpty(idTokenUser.getOAuthUser().getUniqueIdentifier()));
        Assert.assertTrue(Utils.isNotEmpty(accessTokenUser.getOAuthUser().getUniqueIdentifier()));
    }

    /**
     * The goal of this test is to mimic a refresh of an access token by a client.
     * 1. Get an auth code
     * 2. Get an access token
     * 3. Refresh the access token using the refresh token.
     */
    @Test
    public void successGetAuthCodeThenAccessTokenThenRefreshToken() throws Exception {

        // 1. /authorize
        final MvcResult authCodeResult = getAuthCode(true);
        final String redirectedUrl = authCodeResult.getResponse().getRedirectedUrl();
        Assert.assertTrue(Utils.isNotEmpty(redirectedUrl));

        // Check Params
        final Map<String, String> parameters = extractParameters(redirectedUrl);
        Assert.assertNotNull(parameters.get("state"));
        Assert.assertNotNull(parameters.get("code"));

        // 2. /token
        final MvcResult tokenResult = getTokenWithCode(parameters.get("code"), CLIENT_PUBLIC_ID, CLIENT_PASSWD);
        final String contentAsString = tokenResult.getResponse().getContentAsString();
        Assert.assertTrue(Utils.isNotEmpty(contentAsString));

        final Map<String, Object> tokenResponse = objectMapper.readValue(contentAsString, Map.class);
        Assert.assertNotNull(tokenResponse);
        checkToken(tokenResponse, true);


        // 3. /token for refresh
        final MvcResult refreshedToken =
                getAccessTokenFromRefreshToken((String) tokenResponse.get("refresh_token"), CLIENT_PUBLIC_ID, CLIENT_PASSWD);

        String refreshedTokenString = refreshedToken.getResponse().getContentAsString();
        Assert.assertNotNull(refreshedTokenString);
        final Map<String, Object> refreshTokenResponse = objectMapper.readValue(refreshedTokenString, Map.class);
        Assert.assertNotNull(refreshTokenResponse);
        Assert.assertTrue("The response should have an access_token.", refreshTokenResponse.containsKey("access_token"));
        Assert.assertTrue("The response should have an token_type.", refreshTokenResponse.containsKey("refresh_token"));
        Assert.assertFalse("The response should not have an id_token.", refreshTokenResponse.containsKey("id_token"));
        Assert.assertTrue("The response should have an expires_in.", refreshTokenResponse.containsKey("expires_in"));
        Assert.assertTrue("The response should have an token_type.", refreshTokenResponse.containsKey("token_type"));

    }




    private void checkToken(final Map<String, Object> pParameters, boolean pCheckForRefreshToken) {
        Assert.assertTrue("The response should have an id_token.", pParameters.containsKey("id_token"));
        Assert.assertTrue("The response should have an access_token.", pParameters.containsKey("access_token"));
        Assert.assertTrue("The response should have an expires_in.", pParameters.containsKey("expires_in"));
        Assert.assertTrue("The response should have an token_type.", pParameters.containsKey("token_type"));

        if (pCheckForRefreshToken) {
            Assert.assertTrue("The response should have an refresh_token.", pParameters.containsKey("refresh_token"));
        }
    }

    private OAuthUserAndClaim validateToken(String pToken) {

        final OAuthUserAndClaim oAuthUserAndClaim = rsa512JwtTokenVerifier.extractAndValidate(pToken);

        Assert.assertNotNull(oAuthUserAndClaim);

        return oAuthUserAndClaim;
    }

    private Map<String, String> extractParameters(String pUrl) {
        Map<String, String> params = new LinkedHashMap<>();

        // Remove first part
        String paramsUrl = pUrl.replace("http://localhost/login.html", "").substring(1);

        // Split
        final String[] parts = paramsUrl.split("&");

        for (String part : parts) {
            final String[] split = part.split("=");
            params.put(split[0], split[1]);
        }

        return params;
    }


    private RequestBuilder getParams(String pResponseType, boolean pWithRefresh) {

        String[] scopes = pWithRefresh ? new String[] {"openid", "profile", "offline_access"} : new String[] {"openid", "profile"};

        RequestBuilder builder = new RequestBuilder()
                .responseType(pResponseType)
                .clientId(CLIENT_PUBLIC_ID)
                .state(STATE)
                .nonce(NONCE)
                .scopes(scopes)
                .display("desktop")
                .redirection(CLIENT_REDIRECTION);


        return builder;
    }

    public UserDetails userDetails() {

        User user = userRepository.findByUserName(USER_NAME);

        return new AegaeonUserDetails(user.getId(),
                                      user.getUserName(),
                                      user.getPasswd(),
                                      true,
                                      true,
                                      Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }


    private String authorizationHeader(String pClientId, String pClientSecret) {
        return Base64.getEncoder().encodeToString((pClientId + ":" + pClientSecret).getBytes());
    }

    private MvcResult getAuthCode(boolean pWithRefresh) throws Exception {

        LinkedMultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();

        RequestBuilder builder = getParams(FlowUtils.RTYPE_AUTH_CODE, pWithRefresh);
        paramsMap.setAll(builder.build());

        final MvcResult mvcResult = this.mockMvc.perform(post(AuthorizationController.URL)
                                                                 .params(paramsMap)
                                                                 .with(user(userDetails())))
                                                .andDo(print())
                                                .andExpect(status().isFound())
                                                .andExpect(redirectedUrlPattern("http://localhost/login.html*"))
                                                .andReturn();

        return mvcResult;
    }

    private MvcResult getTokenWithCode(String pAuthcode, String pClientId, String pClientSecret) throws Exception {

        String auth = "Basic " + authorizationHeader(pClientId, pClientSecret);

        return this.mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", pAuthcode)
                        .param("grant_type", GrantType.AUTHORIZATION_CODE.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("redirect_uri", "http://localhost/login.html")
                        .header("Authorization", auth))
                           .andDo(print())
                           .andExpect(status().isOk())
                           .andReturn();
    }

    private MvcResult getAccessTokenFromRefreshToken(String pRefreshToken, String pClientId, String pClientSecret) throws Exception {
        String auth = "Basic " + authorizationHeader(pClientId, pClientSecret);

        return this.mockMvc.perform(
                post(TokensController.URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("refresh_token", pRefreshToken)
                        .param("grant_type", GrantType.REFRESH_TOKEN.toString().toLowerCase())
                        .param("client_id", CLIENT_PUBLIC_ID)
                        .param("scope", "openid profile offline_access")
                        .param("redirect_uri", "http://localhost/login.html")
                        .header("Authorization", auth))
                           .andDo(print())
                           .andExpect(status().isOk())
                           .andReturn();
    }

    private MvcResult getUserInfo(String pAccessToken) throws Exception {

        return this.mockMvc.perform(post(UserInfoController.URL)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .header("Authorization", "Bearer " + pAccessToken))
                           .andDo(print())
                           .andExpect(status().isOk())
                           .andReturn();
    }

    private MvcResult getTokenUsingImplicit() throws Exception {
        LinkedMultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();

        RequestBuilder builder = getParams(FlowUtils.RTYPE_IMPLICIT_FULL, false);
        paramsMap.setAll(builder.build());

        final MvcResult mvcResult = this.mockMvc.perform(post(AuthorizationController.URL)
                                                                 .params(paramsMap)
                                                                 .with(user(userDetails())))
                                                .andDo(print())
                                                .andExpect(status().isFound())
                                                .andExpect(redirectedUrlPattern("http://localhost/login.html*"))
                                                .andReturn();

        return mvcResult;
    }

}
