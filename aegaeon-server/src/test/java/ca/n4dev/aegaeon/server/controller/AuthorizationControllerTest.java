package ca.n4dev.aegaeon.server.controller;

import java.util.Arrays;
import java.util.Map;

import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.RequestBuilder;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthorizationControllerTest.java
 *
 * Test AuthorizationController.
 *
 * @author rguillemette
 * @since 2.0.0 - May 08 - 2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class AuthorizationControllerTest /* extends BaseIntegratedControllerTest<AuthorizationController>*/ {

    private static final String USER_NAME = "user@localhost";
    private static final String USER_PASSWD = "user@localhost";
    private static final String CLIENT_PUBLIC_ID = "ca.n4dev.auth.client";
    private static final String CLIENT_REDIRECTION = "http://localhost/login.html";
    private static final String CLIENT_PASSWD = "ca.n4dev.auth.client";
    private static final String STATE = "state1";
    private static final String NONCE = "nonce1";
    private static final String SCOPE = "openid profile";
    private static final String DISPLAY = "desktop";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIfRedirectToLoginPage() throws Exception {
        this.mockMvc.perform(post(AuthorizationController.URL)
                                     .params(asMultiMap(authorizationParams(FlowUtils.RTYPE_AUTH_CODE).build())))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/login"));

    }

    @Test // OK
    public void errorForInvalidMethod() throws Exception {

        this.mockMvc.perform(delete(AuthorizationController.URL)
                                     .params(asMultiMap(authorizationParams(FlowUtils.RTYPE_AUTH_CODE).build()))
                                     .with(user(userDetails())))
                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("http://localhost/login.html?error=invalid_request&state=state1"));
    }

    @Test // ok
    public void errorForInvalidResponseType() throws Exception {

        this.mockMvc.perform(post(AuthorizationController.URL)
                                     .params(asMultiMap(authorizationParams("invalid").build()))
                                     .with(user(userDetails())))
                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("http://localhost/login.html?error=unsupported_response_type&state=state1"));
    }

    @Test // ok
    public void errorForInvalidScope() throws Exception {

        RequestBuilder requestBuilder = authorizationParams(FlowUtils.RTYPE_AUTH_CODE);

        // Teams is not authorized by the user
        requestBuilder.scopes("openid", "teams");

        this.mockMvc.perform(post(AuthorizationController.URL)
                                     .params(asMultiMap(requestBuilder.build()))
                                     .with(user(userDetails())))
                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("http://localhost/login.html?error=invalid_scope&state=state1"));
    }

    @Test // ok
    public void userNeedToAuthorizeClient() throws Exception {

        RequestBuilder requestBuilder = authorizationParams(FlowUtils.RTYPE_AUTH_CODE);
        requestBuilder.clientId("ca.n4dev.auth.client2");

        this.mockMvc.perform(post(AuthorizationController.URL)
                                     .params(asMultiMap(requestBuilder.build()))
                                     .with(user(userDetails())))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andExpect(content().string(new StringContains("form action=\"/authorize/consent\"")));
    }

    @Test // ok
    public void getImplicitFullTokenResponse() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(post(AuthorizationController.URL)
                                                           .params(asMultiMap(authorizationParams(FlowUtils.RTYPE_IMPLICIT_FULL).build()))
                                                           .with(user(userDetails())))
                                          .andDo(print())
                                          .andExpect(status().isFound())
                                          .andExpect(redirectedUrlPattern("http://localhost/login.html*"))
                                          .andReturn();

        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);

        Assert.assertNotNull("Location header is null", locationHeader);
        Assert.assertTrue("Location header is not using fragment parameters.", locationHeader.startsWith("http://localhost/login.html#"));
        Assert.assertTrue("Location header does not contain an id_token.", locationHeader.matches(".*[#&]id_token=.*"));
        Assert.assertTrue("Location header does not contain an access_token.", locationHeader.matches(".*[#&]access_token=.*"));
        Assert.assertTrue("Location header does not contain an expires_in value.", locationHeader.matches(".*[#&]expires_in=.*"));
        Assert.assertTrue("Location header does not contain scope list.", locationHeader.contains("scope=openid%20profile"));
        Assert.assertTrue("Location header does not contain request state.", locationHeader.contains("state=state1"));
        Assert.assertTrue("Location header does not contain request state.", locationHeader.contains("token_type=Bearer"));
        Assert.assertFalse("Location should not have a refresh token", locationHeader.contains("refresh_token="));
    }

    @Test // Not OK
    public void implicitFlowCannotRequestRefreshToken() throws Exception {

        final MultiValueMap<String, String> params = asMultiMap(authorizationParams(FlowUtils.RTYPE_IMPLICIT_FULL).build());
        params.set("scope", "openid profile offline_access");

        MvcResult mvcResult = this.mockMvc.perform(post(AuthorizationController.URL)
                                                           .params(params)
                                                           .with(user(userDetails())))
                                          .andDo(print())
                                          .andExpect(status().isFound())
                                          .andExpect(redirectedUrlPattern("http://localhost/login.html*"))
                                          .andReturn();


        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);

        Assert.assertNotNull("Location header is null", locationHeader);
        Assert.assertFalse("Location should not have a offline_access scope", locationHeader.contains("offline_access"));
        Assert.assertFalse("Location should not have a refresh token", locationHeader.contains("refresh_token="));
        Assert.assertTrue("Location should contain the invalid_scope error.",
                          locationHeader.matches(".*[#&]error=invalid_scope.*"));
        Assert.assertTrue("Location should contain the state.",
                          locationHeader.matches(".*[#&]state=state1.*"));

        //http://localhost/login.html#error=invalid_scope&state=state1]
    }

    @Test // ok
    public void getAuthCodeToken() throws Exception {
        final MvcResult mvcResult = this.mockMvc.perform(post(AuthorizationController.URL)
                                                                 .params(asMultiMap(
                                                                         authorizationParams(FlowUtils.RTYPE_AUTH_CODE).build()))
                                                                 .with(user(userDetails())))
                                                .andDo(print())
                                                .andExpect(status().isFound())
                                                .andExpect(redirectedUrlPattern("http://localhost/login.html*"))
                                                .andReturn();

        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        Assert.assertNotNull("Location header is null", locationHeader);
        Assert.assertTrue("Location header does not contain an code.", locationHeader.matches(".*[\\?&]code=.*"));
        Assert.assertTrue("Location header does not contain request state.", locationHeader.contains("state=state1"));

    }


    private MultiValueMap<String, String> asMultiMap(Map<String, String> pMap) {
        LinkedMultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();

        valueMap.setAll(pMap);

        return valueMap;
    }

    public RequestBuilder authorizationParams(String pResponseType) {

        RequestBuilder builder = new RequestBuilder()
                .responseType(pResponseType)
                .clientId(CLIENT_PUBLIC_ID)
                .state(STATE)
                .nonce(NONCE)
                .scopes("openid", "profile")
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

}
