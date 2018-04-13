package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicJsonException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.AuthorizationService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * AuthorizationControllerUnitTest.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 27 - 2018
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationControllerUnitTest {

    @Mock
    AuthorizationService authorizationService;

    @Mock
    UserAuthorizationService userAuthorizationService;

    @Mock
    AuthorizationCodeService authorizationCodeService;

    @Mock
    TokenServicesFacade tokenServicesFacade;

    private MockMvc mockMvc;
    private AuthorizationController authorizationController;

    @Before
    public void initCtrl() {
        MockitoAnnotations.initMocks(this);

        authorizationController =
                new AuthorizationController(authorizationService, userAuthorizationService, authorizationCodeService, tokenServicesFacade);
        mockMvc = MockMvcBuilders.standaloneSetup(authorizationController).build();
    }

    @Test
    public void successAuthorize() throws Exception {

        doThrow(new OauthRestrictedException(AuthorizationService.class,
                                             OAuthErrorType.invalid_request,
                                             new AuthRequest(FlowUtils.RTYPE_AUTH_CODE),
                                             "test.1",
                                             "https://cool-url.com"))
                .when(authorizationService).validateAuthorizationRequest(new AuthRequest(FlowUtils.RTYPE_AUTH_CODE),
                                                                         RequestMethod.POST,
                                                                         "test.1",
                                                                         "https://cool-url.com",
                                                                         "openid");

        /*
        * mockMvc.perform(get("/users/{id}", 1))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.username", is("Daenerys Targaryen")));

        * */
        mockMvc.perform(get(AuthorizationController.URL)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldGetAuthorizePage() {
        /*
        when(this.authorizationService.validateAuthorizationRequest(anyString(),
                                                                    any(),
                                                                    any(),
                                                                    anyString(),
                                                                    anyString(),
                                                                    anyString())).thenThrow(new OauthRestrictedException());

        */
        /*
        doThrow(new OauthRestrictedException(AuthorizationService.class,
                                             FlowFactory.authCode(),
                                             OAuthErrorType.invalid_request,
                                             anyString(),
                                             anyString())).when(authorizationService).validateAuthorizationRequest(anyString(),
                                                                                                                   RequestMethod.POST,
                                                                                                                   FlowFactory.authCode(),
                                                                                                                   "test.1",
                                                                                                                   anyString(),
                                                                                                                   anyString());
        */
        when(authorizationService.isAuthorized(buildUser(), "test.1")).thenReturn(false);

        //
        // /authorize?client_id=ca.n4dev.auth.client&response_type=auth&scope=openid&redirection_url=https://localhost/login.html&state=42
        ModelAndView view = authorizationController.authorize("auth",
                                                              "test.1",
                                                              "openid",
                                                              "https://localhost/login.html",
                                                              "0xff",
                                                              null,
                                                              null,
                                                              null,
                                                              null,
                                                              buildUser(),
                                                              RequestMethod.POST);

        Assert.assertNotNull("The view should not be null.", view);
        Assert.assertEquals("This should be authorize view.", "authorize", view.getViewName());
        Assert.assertEquals("Incorrect client's id.", "test.1", view.getModel().get("client_id"));
        Assert.assertEquals("Incorrect redirection url.", "https://localhost/login.html", view.getModel().get("redirection_url"));
        Assert.assertEquals("Incorrect scopes.", "openid", view.getModel().get("scopes"));
        Assert.assertEquals("Incorrect state.", "0xff", view.getModel().get("state"));
        Assert.assertEquals("Incorrect response type.", "auth", view.getModel().get("response_type"));
        Assert.assertNull("Display should be null.", view.getModel().get("display"));
        Assert.assertNull("Prompt should be null.", view.getModel().get("prompt"));
    }

    @Test(expected = OAuthPublicJsonException.class)
    public void shouldGetExceptionBecauseOfPromptNone() {
        when(authorizationService.isAuthorized(buildUser(), "test.1")).thenReturn(false);

        ModelAndView view = authorizationController.authorize("auth",
                                                              "test.1",
                                                              "openid",
                                                              "https://localhost/login.html",
                                                              "0xff",
                                                              null,
                                                              Prompt.none.toString(),
                                                              null,
                                                              null,
                                                              buildUser(),
                                                              RequestMethod.POST);

        Assert.fail();
    }

    /**
     * @return An authentication object.
     */
    private Authentication buildUser() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        AegaeonUserDetails userDetails = new AegaeonUserDetails(1L,
                                                                "tester",
                                                                "passwd",
                                                                true,
                                                                true,
                                                                authorities);


        return new UsernamePasswordAuthenticationToken(userDetails, "passwd", authorities);
    }


    /*
    * authorize(@RequestParam("response_type") String pResponseType,
                                  @RequestParam("client_id") String pClientPublicId,
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                  @RequestParam(value = "state", required = false) String pState,
                                  @RequestParam(value = "nonce", required = false) String pNonce,
                                  @RequestParam(value = "prompt", required = false) String pPrompt,
                                  @RequestParam(value = "display", required = false) String pDisplay,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {
    * */

    // authorize
    // addUserAuthorization
    //

    /*
    * @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .addFilters(new CORSFilter())
                .build();
    }
    * */

}
