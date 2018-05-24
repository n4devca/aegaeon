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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthorizationControllerTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - May 08 - 2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class AuthorizationControllerTest {

    private static final String USER_NAME = "user@localhost";
    private static final String USER_PASSWD = "user@localhost";
    private static final String CLIENT_PUBLIC_ID = "ca.n4dev.auth.client";
    private static final String CLIENT_REDIRECTION = "http://localhost/login.html";
    private static final String CLIENT_PASSWD = "ca.n4dev.auth.client";
    private static final String STATE = "state1";
    private static final String NONCE = "nonce1";
    private static final String SCOPE = "openid profile";
    private static final String DISPLAY = "desktop";



    /*
    * private UserAuthorizationService userAuthorizationService;
    private AuthorizationCodeService authorizationCodeService;
    private AuthorizationService authorizationService;
    private TokenServicesFacade tokenServicesFacade;

    @RequestMapping(value = "")
    public ModelAndView authorize(@RequestParam(value = "response_type", required = false) String pResponseType,
                                  @RequestParam(value = "client_id", required = false) String pClientPublicId,
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                  @RequestParam(value = "state", required = false) String pState,
                                  @RequestParam(value = "nonce", required = false) String pNonce,
                                  @RequestParam(value = "prompt", required = false) String pPrompt,
                                  @RequestParam(value = "display", required = false) String pDisplay,
                                  @RequestParam(value = "id_token_hint", required = false) String pIdTokenHint,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {


        this.mockMvc
                .perform(post(IntrospectController.URL).params(introspectParams(true))
                                                       .with(user(USER_NAME)
                                                                     .password(CLIENT_PASSWD)
                                                                     .authorities(new SimpleGrantedAuthority(
                                                                             "ROLE_CLIENT"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
    * */


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void errorForInvalidMethod() throws Exception {

        this.mockMvc.perform(delete(AuthorizationController.URL)
                                     .params(asMultiMap(authorizationParams(FlowUtils.RTYPE_AUTH_CODE).build()))
                                     .with(user(USER_NAME)
                                                   .password(USER_PASSWD)
                                                   .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("http://localhost/login.html?error=invalid_request&state=state1"));
    }

    @Test
    public void errorForInvalidResponseType() throws Exception {

        this.mockMvc.perform(post(AuthorizationController.URL)
                                     .params(asMultiMap(authorizationParams("invalid").build()))
                                     .with(user(userDetails())))
                    .andDo(print())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("http://localhost/login.html?error=unsupported_response_type&state=state1"));
    }

    @Test
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

    @Test
    public void userNeedToAuthorizeClient() throws Exception {

        RequestBuilder requestBuilder = authorizationParams(FlowUtils.RTYPE_AUTH_CODE);
        requestBuilder.clientId("ca.n4dev.auth.client2");

        this.mockMvc.perform(post(AuthorizationController.URL)
                                     .params(asMultiMap(requestBuilder.build()))
                                     .with(user(userDetails())))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andExpect(content().string(new StringContains("form action=\"/authorize/accept\"")));
    }

    @Test
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
        Assert.assertTrue("Location header does not contain an id_token.", locationHeader.matches(".*[\\?&]id_token=.*"));
        Assert.assertTrue("Location header does not contain an access_token.", locationHeader.matches(".*[\\?&]access_token=.*"));
        Assert.assertTrue("Location header does not contain an expires_in value.", locationHeader.matches(".*[\\?&]expires_in=.*"));
        Assert.assertTrue("Location header does not contain scope list.", locationHeader.contains("scope=openid%20profile"));
        Assert.assertTrue("Location header does not contain request state.", locationHeader.contains("state=state1"));
        Assert.assertTrue("Location header does not contain request state.", locationHeader.contains("token_type=bearer"));


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