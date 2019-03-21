package ca.n4dev.aegaeon.server.service;

import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.repository.AccessTokenRepository;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.view.TokenView;
import ca.n4dev.aegaeon.server.view.mapper.TokenMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnitRunner;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


/**
 * AccessTokenServiceUnitTest.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 25 - 2018
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessTokenServiceUnitTest {


    @Mock
    AccessTokenRepository accessTokenRepository;

    @Mock
    TokenFactory tokenFactory;

    @Mock
    UserService userService;

    @Mock
    ClientService clientService;

    @Mock
    UserAuthorizationService userAuthorizationService;

    @Mock
    TokenMapper tokenMapper;

    @Test
    public void successFindByTokenValue() {
        // public TokenView findByTokenValue(String pTokenValue) {

        AccessToken accessToken = buildAccessToken();

        when(accessTokenRepository.findByToken(any())).thenAnswer(a -> accessToken);

        when(tokenMapper.toView(accessToken)).thenAnswer(a -> {
            AccessToken token = a.getArgument(0);
            TokenView view = new TokenView();

            view.setId(token.getId());
            view.setScopes(token.getScopes());
            view.setToken(token.getToken());
            view.setValidUntil(token.getValidUntil());
            view.setTokenType(token.getTokenType().toString());

            return view;
        });


        AccessTokenService accessTokenService = new AccessTokenService(accessTokenRepository,
                                                                       tokenFactory,
                                                                       userService,
                                                                       clientService,
                                                                       userAuthorizationService,
                                                                       tokenMapper);

        TokenView accessTokenView = accessTokenService.findByTokenValue("0xff");
        Assert.assertNotNull("Access Token View should not be null.", accessTokenView);
        Assert.assertEquals("ID should be the same.", accessToken.getId(), accessTokenView.getId());
        Assert.assertEquals("Token Type should be the same.", accessToken.getTokenType().toString(), accessTokenView.getTokenType());
        Assert.assertEquals("Scope should be the same.", accessToken.getScopes(), accessTokenView.getScopes());
        Assert.assertTrue("Valid Date should be the same.", accessToken.getValidUntil().isEqual(accessTokenView.getValidUntil()));
    }

    private AccessToken buildAccessToken() {
        AccessToken token = new AccessToken();
        token.setToken("0xff");
        token.setScopes(String.join(" ", "openid", "profile"));
        token.setValidUntil(ZonedDateTime.now(ZoneOffset.UTC).plus(1L, ChronoUnit.DAYS));
        token.setId(1L);
        token.setClient(new Client(42L));

        return token;
    }
}
