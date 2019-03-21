package ca.n4dev.aegaeon.server.service;

import ca.n4dev.aegaeon.api.token.*;
import ca.n4dev.aegaeon.api.token.payload.Claims;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.api.token.verifier.TokenVerifier;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import ca.n4dev.aegaeon.server.token.provider.HMAC512JwtTokenProvider;
import ca.n4dev.aegaeon.server.token.provider.RSA512JwtTokenProvider;
import ca.n4dev.aegaeon.server.token.verifier.HMAC512JwtTokenVerifier;
import ca.n4dev.aegaeon.server.token.verifier.RSA512JwtTokenVerifier;
import ca.n4dev.aegaeon.server.utils.Utils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.Mockito.when;

/**
 * TokenProviderUnitTest.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 23 - 2018
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenProviderUnitTest {

    private static final String TESTING_JWKS = "testing.jwks";

    @Mock
    KeysProvider keysProvider;

    @Test
    public void successCreateRSA512Token() throws Exception {
        setupKeysProvider();
        ServerInfo serverInfo = buildServerInfo();
        TokenProvider tokenProvider = new RSA512JwtTokenProvider(this.keysProvider, serverInfo);
        TokenVerifier tokenVerifier = new RSA512JwtTokenVerifier(keysProvider, serverInfo);

        Token token = tokenProvider.createToken(buildOAuthUser(), buildOAuthClient(TokenProviderType.RSA_RS512), 1L, ChronoUnit.DAYS);
        validateToken(token, tokenVerifier);
    }

    @Test
    public void successCreateRSA512TokenWithPayload() throws Exception {
        setupKeysProvider();
        ServerInfo serverInfo = buildServerInfo();
        TokenProvider tokenProvider = new RSA512JwtTokenProvider(this.keysProvider, serverInfo);
        TokenVerifier tokenVerifier = new RSA512JwtTokenVerifier(keysProvider, serverInfo);

        Token token = tokenProvider.createToken(buildOAuthUser(),
                                                buildOAuthClient(TokenProviderType.RSA_RS512),
                                                1L,
                                                ChronoUnit.DAYS,
                                                Utils.asMap(Claims.WEBSITE, "httpz://bob.com", Claims.PHONE_NUMBER, "+1-111-111-1111"));

        validateToken(token, tokenVerifier);

        // Validate Claim
        OAuthUserAndClaim userAndClaim = tokenVerifier.extractAndValidate(token.getValue());
        Assert.assertNotNull("Verifier was not able to extract OAuthUser and claims.", userAndClaim);
        Assert.assertNotNull("Claims should not be empty.", userAndClaim.getClaims());
        Assert.assertTrue("We should have at least 2 claims.", userAndClaim.getClaims().size() >= 2);
        Assert.assertNotNull("No website claim.", userAndClaim.getClaims().get(Claims.WEBSITE));
        Assert.assertNotNull("No phone number claim.", userAndClaim.getClaims().get(Claims.PHONE_NUMBER));
    }

    @Test
    public void successCreateHMAC512Token() throws Exception {
        setupKeysProvider();
        ServerInfo serverInfo = buildServerInfo();
        TokenProvider tokenProvider = new HMAC512JwtTokenProvider(this.keysProvider, serverInfo);
        TokenVerifier tokenVerifier = new HMAC512JwtTokenVerifier(this.keysProvider, serverInfo);

        Token token = tokenProvider.createToken(buildOAuthUser(),
                                                buildOAuthClient(TokenProviderType.HMAC_HS512),
                                                1L, ChronoUnit.DAYS);

        validateToken(token, tokenVerifier);

    }

    @Test
    public void successCreateHMAC512TokenWithPayload() throws Exception {
        setupKeysProvider();
        ServerInfo serverInfo = buildServerInfo();
        TokenProvider tokenProvider = new HMAC512JwtTokenProvider(this.keysProvider, serverInfo);
        TokenVerifier tokenVerifier = new HMAC512JwtTokenVerifier(this.keysProvider, serverInfo);

        Token token = tokenProvider.createToken(buildOAuthUser(),
                                                buildOAuthClient(TokenProviderType.HMAC_HS512),
                                                1L, ChronoUnit.DAYS,
                                                Utils.asMap(Claims.WEBSITE, "httpz://bob.com", Claims.PHONE_NUMBER, "+1-111-111-1111"));

        validateToken(token, tokenVerifier);
        OAuthUserAndClaim userAndClaim = tokenVerifier.extractAndValidate(token.getValue());
        Assert.assertNotNull("Verifier was not able to extract OAuthUser and claims.", userAndClaim);
        Assert.assertNotNull("Claims should not be empty.", userAndClaim.getClaims());
        Assert.assertTrue("We should have at least 2 claims.", userAndClaim.getClaims().size() >= 2);
        Assert.assertNotNull("No website claim.", userAndClaim.getClaims().get(Claims.WEBSITE));
        Assert.assertNotNull("No phone number claim.", userAndClaim.getClaims().get(Claims.PHONE_NUMBER));

    }

    private void validateToken(Token pToken, TokenVerifier pTokenVerifier) {
        Assert.assertNotNull("A token should have been created.", pToken);
        Assert.assertNotNull("The token has no value.", pToken.getValue());
        Assert.assertNotNull("The token has no end date.", pToken.getValidUntil());
        Assert.assertTrue("The end date is invalid (not after now).",
                          pToken.getValidUntil().isAfter(ZonedDateTime.now(ZoneOffset.UTC)));

        // Tomorrow or equals
        final ZonedDateTime tomorrow = ZonedDateTime.now(ZoneOffset.UTC).plus(1L, ChronoUnit.DAYS);
        Assert.assertTrue("The end date is invalid (not before -1d).",
                          pToken.getValidUntil().isBefore(tomorrow) || pToken.getValidUntil().isEqual(tomorrow));
        Assert.assertTrue("The token's value (JWT) is invalid.", pTokenVerifier.validate(pToken.getValue()));
    }

    private OAuthClient buildOAuthClient(TokenProviderType pTokenProviderType) {
        return new OAuthClient() {
            @Override
            public String getClientId() {
                return "testing.client.1";
            }

            @Override
            public String getProviderName() {
                return pTokenProviderType.toString();
            }
        };
    }

    private OAuthUser buildOAuthUser() {
        return new OAuthUser() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getUniqueIdentifier() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getName() {
                return "unit tester";
            }
        };
    }

    private ServerInfo buildServerInfo() {
        return new ServerInfo("ca.n4dev.aegaeon.unittest");
    }

    private void setupKeysProvider() {
        when(keysProvider.getJwkSet()).thenAnswer(a -> {
            ClassLoader cl = getClass().getClassLoader();
            File f = new File(cl.getResource(TESTING_JWKS).getFile());
            JWKSet keySet = JWKSet.load(f);


            return keySet;
        });

    }
}
