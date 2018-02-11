package ca.n4dev.aegaeon.api.token;

import java.util.Map;

/**
 * OAuthUserAndClaim.java
 *
 * An OauthUser and the claims extract from a token validation.
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 24 - 2018
 */
public interface OAuthUserAndClaim {

    /**
     * @return the oAuthUser
     */
    OAuthUser getOAuthUser();

    /**
     * @return the claims
     */
    Map<String, String> getClaims();
}
