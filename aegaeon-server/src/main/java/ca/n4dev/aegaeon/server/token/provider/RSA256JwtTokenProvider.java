package ca.n4dev.aegaeon.server.token.provider;

import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import org.springframework.stereotype.Component;

/**
 * RSA256JwtTokenProvider.java
 * <p>
 * A TokenProvider producing RSA (256) token.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 16 - 2019
 */
@Component
public class RSA256JwtTokenProvider extends BaseRSAJwtTokenProvider {

    /**
     * Default Constructor.
     *
     * @param pKeysProvider The key store of the server.
     * @param pServerInfo
     * @throws JOSEException
     */
    public RSA256JwtTokenProvider(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        super(pKeysProvider, pServerInfo);
    }

    @Override
    protected JWSAlgorithm getJWSAlgorithm() {
        return JWSAlgorithm.RS256;
    }

    @Override
    public TokenProviderType getType() {
        return TokenProviderType.RSA_RS256;
    }
}
