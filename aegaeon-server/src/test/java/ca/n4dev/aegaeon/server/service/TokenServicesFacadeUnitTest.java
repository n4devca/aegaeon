package ca.n4dev.aegaeon.server.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.*;
import ca.n4dev.aegaeon.api.model.*;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.view.ScopeView;
import ca.n4dev.aegaeon.server.view.TokenResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.mockito.Mockito.*;

/**
 * TokenServicesFacadeUnitTest.java
 * <p>
 * Test TokenServicesFacade.
 * <p>
 * This unit test class is fairly intensive because TokenServicesFacade is
 * core to Aegaeon.
 * <p>
 * We are not testing individual token creation here.
 * Every *TokenService has its unit test.
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 26 - 2018
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenServicesFacadeUnitTest {

    @Mock
    IdTokenService idTokenService;

    @Mock
    AccessTokenService accessTokenService;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    ScopeService scopeService;

    @Mock
    AuthorizationCodeService authorizationCodeService;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    ClientService clientService;

    TokenServicesFacade facade;

    @Before
    public void buildFacade() {
        facade = new TokenServicesFacade(idTokenService,
                                         accessTokenService,
                                         refreshTokenService,
                                         scopeService,
                                         authorizationCodeService,
                                         clientService,
                                         eventPublisher);
    }

    @Test
    public void successCreateTokenForAuthCode() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(buildFlow(Flow.authorization_code));
        when(this.authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("auth-code-0xff",
                                                                                             "test.1",
                                                                                             "https://cool-place.com/"));


        mockCreateToken();

        try {

            TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                         "auth-code-0xff",
                                                         "test.1",
                                                         "https://cool-place.com/",
                                                         "openid profile",
                                                         null);

            TokenResponse tokenResponse = facade.createTokenForAuthCode(tokenRequest,
                                                                        buildUser());

            Assert.assertNotNull(tokenResponse);

        } catch (Exception pException) {

            Assert.fail(pException.getMessage());
        }


    }

    @Test
    public void successCreateTokenForRefreshToken() {

    }

    @Test
    public void successCreateTokenForClientCred() {

    }

    @Test
    public void successCreateTokenForImplicit() {
        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(buildFlow(Flow.implicit));
        //when(clientService.findScopeByClientId(anyLong())).thenReturn(buildScope("openid profile"));
        mockCreateToken();

        try {

            final AuthRequest authRequest =
                    new AuthRequest(FlowUtils.RTYPE_AUTH_CODE, "openid profile",
                                    "test.1", "https://cool-place.com/");


            TokenResponse tokenResponse = facade.createTokenForImplicit(authRequest,
                                                                        buildUser());

            Assert.assertNotNull(tokenResponse);
        } catch (Exception pException) {

            Assert.fail(pException.getMessage());
        }
    }

    @Test(expected = InternalAuthorizationException.class)
    public void failCreateTokenForAuthCodeBecauseNoAuth() {

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://no-where.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      null);
    }

    @Test(expected = InvalidClientIdException.class)
    public void failCreateTokenForAuthCodeBecauseMissingClientId() {
        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     null,
                                                     "https://no-where.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidAuthorizationCodeException.class)
    public void failCreateTokenForAuthCodeBecauseMissingAuthCode() {
        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     null,
                                                     "test.1",
                                                     "https://no-where.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidClientRedirectionException.class)
    public void failCreateTokenForAuthCodeBecauseMissingRedirect() {
        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     null,
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidClientIdException.class)
    public void failCreateTokenForAuthCodeBecauseUnkownClient() {

        when(clientService.findByPublicId(anyString())).thenReturn(null);

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://no-where.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidClientRedirectionException.class)
    public void failCreateTokenForAuthCodeBecauseNoRedirection() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(null);

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://no-where.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidClientRedirectionException.class)
    public void failCreateTokenForAuthCodeBecauseInvalidRedirection() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://no-where.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = UnauthorizedGrant.class)
    public void failCreateTokenForAuthCodeBecauseNoGrant() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(null);

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://cool-place.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = UnauthorizedGrant.class)
    public void failCreateTokenForAuthCodeBecauseGrantNotAuthorized() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(buildFlow(Flow.implicit));

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://cool-place.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidAuthorizationCodeException.class)
    public void failCreateTokenForAuthCodeBecauseBadAuthCode() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(buildFlow(Flow.authorization_code));
        when(authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("other-auth-code-0xff",
                                                                                        "test.1",
                                                                                        "https://cool-place.com/"));

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://cool-place.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidAuthorizationCodeException.class)
    public void failCreateTokenForAuthCodeBecauseNotSameClient() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(buildFlow(Flow.authorization_code));
        when(authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("auth-code-0xff",
                                                                                        "test.2",
                                                                                        "https://cool-place.com/"));

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://cool-place.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
    }

    @Test(expected = InvalidAuthorizationCodeException.class)
    public void failCreateTokenForAuthCodeBecauseNotRedirect() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByClientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findAuthFlowByClientId(anyLong())).thenReturn(buildFlow(Flow.authorization_code));
        when(authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("auth-code-0xff",
                                                                                        "test.1",
                                                                                        "https://not-a-cool-place.com/"));

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString().toLowerCase(),
                                                     "auth-code-0xff",
                                                     "test.1",
                                                     "https://cool-place.com/",
                                                     "openid profile",
                                                     null);

        facade.createTokenForAuthCode(tokenRequest,
                                      buildUser());
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

    private Client buildClient(String pPublicId) {
        Client client = new Client();

        client.setId(1000L);
        client.setPublicId(pPublicId);

        // Add more

        return client;
    }

    private List<ClientAuthFlow> buildFlow(Flow pFlow) {
        List<ClientAuthFlow> grants = new ArrayList<>();

        grants.add(new ClientAuthFlow(buildClient("test.1"), pFlow));

        return grants;
    }

    private List<ClientScope> buildScope(String pScopeStr) {
        ArrayList<ClientScope> clientScopes = new ArrayList<>();

        for (String onScopeStr : pScopeStr.split(" ")) {
            Scope scope = new Scope();
            scope.setName(onScopeStr);
            clientScopes.add(new ClientScope(null, scope));
        }

        return clientScopes;
    }

    private AuthorizationCode buildAuthCode(String pCode, String pClientId, String pRedirection) {
        AuthorizationCode authorizationCode = new AuthorizationCode(pCode, null, buildClient(pClientId), pRedirection);

        authorizationCode.setScopes("id_token openid profile");

        User user = new User();
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));

        user.setId(1L);
        user.setUserName("tester");
        user.setEnabled(true);
        user.setAuthorities(authorities);

        authorizationCode.setUser(user);

        return authorizationCode;
    }

    private List<ClientRedirection> buildRedirections() {
        List<ClientRedirection> redirections = new ArrayList<>();
        redirections.add(new ClientRedirection(buildClient("test.1"), "https://cool-place.com/"));

        return redirections;
    }

    private void mockCreateToken() {

        when(scopeService.getValidScopes(anyString())).thenAnswer(a -> {
            String scopeArg = a.getArgument(0);
            String[] scopesStr = scopeArg.split(" ");
            Set<ScopeView> lst = new HashSet<>();

            for (String s : scopesStr) {
                ScopeView scope = new ScopeView();
                scope.setName(s);
                lst.add(scope);
            }

            return lst;
        });

        when(idTokenService.createToken(any(), anyLong(), any(), anySet())).thenAnswer(a -> {
            IdToken idToken = new IdToken();
            idToken.setValidUntil(ZonedDateTime.now(ZoneOffset.UTC).plus(15L, ChronoUnit.MINUTES));
            return idToken;
        });

        when(accessTokenService.createToken(any(), anyLong(), any(), anySet())).thenAnswer(a -> {
            AccessToken accessToken = new AccessToken();
            accessToken.setValidUntil(ZonedDateTime.now(ZoneOffset.UTC).plus(60L, ChronoUnit.MINUTES));
            return accessToken;
        });

        when(refreshTokenService.createToken(any(), anyLong(), any(), anySet())).thenAnswer(a -> {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setValidUntil(ZonedDateTime.now(ZoneOffset.UTC).plus(60L, ChronoUnit.DAYS));
            return refreshToken;
        });

    }
}
