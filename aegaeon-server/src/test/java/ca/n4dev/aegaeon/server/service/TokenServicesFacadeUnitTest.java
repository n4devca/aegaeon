package ca.n4dev.aegaeon.server.service;

import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.model.*;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.view.TokenResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * TokenServicesFacadeUnitTest.java
 * <p>
 * Test TokenServicesFacade.
 * <p>
 * This unit test class is fairly intensive because TokenServicesFacade is
 * core to Aegaeon.
 *
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
    ClientService clientService;

    TokenServicesFacade facade;

    @Before
    public void buildFacade() {
        facade = new TokenServicesFacade(idTokenService,
                                         accessTokenService,
                                         refreshTokenService,
                                         scopeService,
                                         authorizationCodeService,
                                         clientService);
    }

    @Test
    public void successCreateTokenForAuthCode() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findGrantTypesByclientId(anyLong())).thenReturn(buildGrants(GrantType.CODE_AUTH_CODE));
        when(this.authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("auth-code-0xff",
                                                                                                             "test.1",
                                                                                                             "https://cool-place.com/"));


        mockCreateToken();
        
        try {
            TokenResponse tokenResponse = facade.createTokenForAuthCode("test.1",
                                                        "auth-code-0xff",
                                                        "https://cool-place.com/",
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

    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseNoAuth() {

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://no-where.com/",
                                      null);
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseMissingClientId() {
        facade.createTokenForAuthCode(null,
                                      "auth-code-0xff",
                                      "https://no-where.com/",
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseMissingAuthCode() {
        facade.createTokenForAuthCode("test.1",
                                      null,
                                      "https://no-where.com/",
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseMissingRedirect() {
        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      null,
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseUnkownClient() {

        when(clientService.findByPublicId(anyString())).thenReturn(null);

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://no-where.com/",
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseNoRedirection() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(null);

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://no-where.com/",
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseInvalidRedirection() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://no-where.com/",
                                      buildUser());
    }

    @Test(expected = OAuthPublicRedirectionException.class)
    public void failCreateTokenForAuthCodeBecauseNoGrant() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findGrantTypesByclientId(anyLong())).thenReturn(null);

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://cool-place.com/",
                                      buildUser());
    }

    @Test(expected = OAuthPublicRedirectionException.class)
    public void failCreateTokenForAuthCodeBecauseGrantNotAuthorized() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findGrantTypesByclientId(anyLong())).thenReturn(buildGrants(GrantType.CODE_IMPLICIT));

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://cool-place.com/",
                                      buildUser());
    }

    @Test(expected = OAuthPublicRedirectionException.class)
    public void failCreateTokenForAuthCodeBecauseBadAuthCode() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findGrantTypesByclientId(anyLong())).thenReturn(buildGrants(GrantType.CODE_AUTH_CODE));
        when(authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("other-auth-code-0xff",
                                                                                                        "test.1",
                                                                                                        "https://cool-place.com/"));

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://cool-place.com/",
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseNotSameClient() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findGrantTypesByclientId(anyLong())).thenReturn(buildGrants(GrantType.CODE_AUTH_CODE));
        when(authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("auth-code-0xff",
                                                                                                        "test.2",
                                                                                                        "https://cool-place.com/"));

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://cool-place.com/",
                                      buildUser());
    }

    @Test(expected = OauthRestrictedException.class)
    public void failCreateTokenForAuthCodeBecauseNotRedirect() {

        when(clientService.findByPublicId(anyString())).thenReturn(buildClient("test.1"));
        when(clientService.findRedirectionsByclientId(anyLong())).thenReturn(buildRedirections());
        when(clientService.findGrantTypesByclientId(anyLong())).thenReturn(buildGrants(GrantType.CODE_AUTH_CODE));
        when(authorizationCodeService.findByCode(anyString())).thenReturn(buildAuthCode("auth-code-0xff",
                                                                                                        "test.1",
                                                                                                        "https://not-a-cool-place.com/"));

        facade.createTokenForAuthCode("test.1",
                                      "auth-code-0xff",
                                      "https://cool-place.com/",
                                      buildUser());
    }

    /**
     * @return An authentication object.
     */
    private Authentication buildUser() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        SpringAuthUserDetails userDetails = new SpringAuthUserDetails(1L,
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

    private List<ClientGrantType> buildGrants(String pGrantCode) {
        List<ClientGrantType> grants = new ArrayList<>();

        GrantType g = new GrantType();
        g.setCode(pGrantCode);

        grants.add(new ClientGrantType(buildClient("test.1"), g, true));

        return grants;
    }

    private AuthorizationCode buildAuthCode(String pCode, String pClientId, String pRedirection) {
        AuthorizationCode authorizationCode = new AuthorizationCode(pCode, null, buildClient(pClientId), pRedirection);

        authorizationCode.setScopes("id_token openid profile");

        return authorizationCode;
    }

    private List<ClientRedirection> buildRedirections() {
        List<ClientRedirection> redirections = new ArrayList<>();
        redirections.add(new ClientRedirection(buildClient("test.1"), "https://cool-place.com/"));

        return redirections;
    }
    
    private void mockCreateToken() {

        when(scopeService.findScopeFromString(anyString())).thenAnswer(a -> {
           String scopeArg = a.getArgument(0);
           String[] scopesStr = scopeArg.split(" ");
           List<Scope> lst = new ArrayList<>();

           for (String s : scopesStr) {
               Scope scope = new Scope();
               scope.setName(s);
               lst.add(scope);
           }

           return lst;
        });

        when(idTokenService.createToken(any(), anyLong(), any(), anyList())).thenAnswer(a -> {
            IdToken idToken = new IdToken();
            idToken.setValidUntil(LocalDateTime.now().plus(15L, ChronoUnit.MINUTES));
            return idToken;
        });

        when(accessTokenService.createToken(any(), anyLong(), any(), anyList())).thenAnswer(a -> {
            AccessToken accessToken = new AccessToken();
            accessToken.setValidUntil(LocalDateTime.now().plus(60L, ChronoUnit.MINUTES));
            return accessToken;
        });

        when(refreshTokenService.createToken(any(), anyLong(), any(), anyList())).thenAnswer(a -> {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setValidUntil(LocalDateTime.now().plus(60L, ChronoUnit.DAYS));
            return refreshToken;
        });

    }
}
