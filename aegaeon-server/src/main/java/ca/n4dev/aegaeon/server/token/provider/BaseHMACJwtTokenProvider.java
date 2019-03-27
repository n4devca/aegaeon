package ca.n4dev.aegaeon.server.token.provider;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * BaseHMACJwtTokenProvider.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 18 - 2019
 */
public abstract class BaseHMACJwtTokenProvider implements TokenProvider {

    protected ServerInfo serverInfo;
    protected JWSSigner signer;
    protected boolean enabled = false;
    protected String keyId;

    protected abstract JWSAlgorithm getJWSAlgorithm();

    /**
     * @throws JOSEException
     *
     */
    @Autowired
    public BaseHMACJwtTokenProvider(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        this.serverInfo = pServerInfo;

        JWKSet keySet = pKeysProvider.getJwkSet();

        for (JWK jwk : keySet.getKeys()) {

            if (jwk.isPrivate()) {

                if (jwk instanceof OctetSequenceKey) {
                    keyId = jwk.getKeyID();
                    this.signer = new MACSigner((OctetSequenceKey) jwk);
                    break;
                }
            }
        }

        if (this.signer != null) {
            this.enabled = true;
        }
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        return createToken(pOAuthUser, pOAuthClient, pTimeValue, pTemporalUnit, Collections.emptyMap());
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


    /**
     * @return the keyId
     */
    public String getKeyId() {
        return keyId;
    }


    @Override
    public String getAlgorithmName() {
        return getJWSAlgorithm().toString();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit, java.util.List)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit,
                             Map<String, Object> pPayloads) throws Exception {

        ZonedDateTime expiredIn = ZonedDateTime.now(ZoneOffset.UTC).plus(pTimeValue, pTemporalUnit);
        Instant instant = expiredIn.toInstant();
        Date date = Date.from(instant);

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        builder.expirationTime(date);
        builder.issuer(this.serverInfo.getIssuer());
        builder.subject(pOAuthUser.getUniqueIdentifier());
        builder.audience(pOAuthClient.getClientId());
        builder.issueTime(new Date());

        if (pPayloads != null && !pPayloads.isEmpty()) {
            for (Map.Entry<String, Object> en : pPayloads.entrySet()) {
                builder.claim(en.getKey(), en.getValue());
            }
        }

        JWTClaimsSet claimsSet = builder.build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(getJWSAlgorithm()), claimsSet);
        signedJWT.sign(this.signer);

        Token token = new Token(signedJWT.serialize(), expiredIn);

        return token;
    }


}
