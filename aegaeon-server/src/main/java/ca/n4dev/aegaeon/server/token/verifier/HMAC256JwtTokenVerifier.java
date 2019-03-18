package ca.n4dev.aegaeon.server.token.verifier;

import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.verifier.TokenVerifier;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.stereotype.Component;

/**
 * HMAC256JwtTokenVerifier.java
 *
 * HMAC / 256 JWT verifier.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 17 - 2019
 */
@Component
public class HMAC256JwtTokenVerifier extends BaseJwtVerifier implements TokenVerifier {

    private boolean enable = true;

    private JWSVerifier verifier = null;

    public HMAC256JwtTokenVerifier(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        super(TokenProviderType.HMAC_HS256, pServerInfo);

        JWKSet keySet = pKeysProvider.getJwkSet();

        for (JWK jwk : keySet.getKeys()) {

            if (jwk instanceof OctetSequenceKey) {
                this.verifier = new MACVerifier(((OctetSequenceKey) jwk).toByteArray());
                this.enable = true;
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#isEnable()
     */
    @Override
    public boolean isEnable() {
        return this.enable;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.verifier.BaseJwtVerifier#getJWSVerifier()
     */
    @Override
    protected JWSVerifier getJWSVerifier() {
        return this.verifier;
    }
}
