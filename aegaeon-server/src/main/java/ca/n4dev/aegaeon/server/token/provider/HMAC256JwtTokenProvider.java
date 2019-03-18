package ca.n4dev.aegaeon.server.token.provider;

import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import org.springframework.stereotype.Component;

/**
 * HMAC256JwtTokenProvider.java
 * <p>
 * A TokenProvider producing HMAC token.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 16 - 2019
 */
@Component
public class HMAC256JwtTokenProvider extends BaseHMACJwtTokenProvider {

    /**
     * @param pKeysProvider
     * @param pServerInfo
     * @throws JOSEException
     */
    public HMAC256JwtTokenProvider(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        super(pKeysProvider, pServerInfo);
    }

    @Override
    protected JWSAlgorithm getJWSAlgorithm() {
        return JWSAlgorithm.HS256;
    }


    @Override
    public TokenProviderType getType() {
        return TokenProviderType.HMAC_HS256;
    }

}
