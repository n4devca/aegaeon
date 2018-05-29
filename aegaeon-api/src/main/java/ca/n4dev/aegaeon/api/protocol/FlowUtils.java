package ca.n4dev.aegaeon.api.protocol;

import ca.n4dev.aegaeon.api.exception.OpenIdExceptionBuilder;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;

/**
 * FlowCreator.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 11 - 2018
 */
public class FlowUtils {


    /*
     * /Token
     * >>>> grant_type
     * > openid
     * authorization_code
     * refresh_token
     * > oauth
     * password
     * client_credentials
     * refresh_token
     * authorization_code
     *
     * /authorize
     * >>>> response_type
     * > openid
     * code = auth flow
     * id_token [token] = implicit flow
     * > oauth
     * code = auth flow
     * token = implicit flow
     */

    public static final String RTYPE_AUTH_CODE = "code";
    public static final String RTYPE_IMPLICIT_FULL = "id_token token";
    public static final String RTYPE_IMPLICIT_ONLYID = "id_token";
    public static final String RTYPE_HYBRID_ONLYID = "code id_token";
    public static final String RTYPE_HYBRID_FULL = "code id_token token";
    public static final String RTYPE_HYBRID_NOID = "code token";


    public static GrantType getAuthorizationType(AuthRequest pAuthRequest) {

        if (pAuthRequest == null) {
            throw new OpenIdExceptionBuilder()
                    .code(ServerExceptionCode.RESPONSETYPE_INVALID)
                    .build();
            //throw new OauthRestrictedException(FlowUtils.class, OpenIdErrorType.invalid_grant, pAuthRequest, null, null);
        }

        return getAuthorizationType(pAuthRequest.getResponseTypeParam());
    }


    public static GrantType getAuthorizationType(String pResponseTypeParam) {

        if (pResponseTypeParam == null || pResponseTypeParam.isEmpty()) {
            throw new OpenIdExceptionBuilder()
                    .code(ServerExceptionCode.RESPONSETYPE_INVALID)
                    .build();
            //throw new OauthRestrictedException(FlowUtils.class, OpenIdErrorType.invalid_grant, pAuthRequest, null, null);
        }

        /*
         * code => Authorization Code
         * id_token [token] => implicit
         * token => implicit (oauth)
         * code id_token => hybrid
         * code token => hybrid
         * code id_token token => hybrid
         */
        String responseTypeParam = pResponseTypeParam;

        if (RTYPE_AUTH_CODE.equalsIgnoreCase(responseTypeParam)) {
            return GrantType.AUTHORIZATION_CODE;
        } else if (RTYPE_IMPLICIT_ONLYID.equalsIgnoreCase(responseTypeParam) || RTYPE_IMPLICIT_FULL
                .equalsIgnoreCase(responseTypeParam)) {
            return GrantType.IMPLICIT;
        } else if (RTYPE_HYBRID_ONLYID.equalsIgnoreCase(responseTypeParam)
                || RTYPE_HYBRID_FULL.equalsIgnoreCase(responseTypeParam)
                || RTYPE_HYBRID_NOID.equalsIgnoreCase(responseTypeParam)) {
            return GrantType.HYBRID;
        }

        return null;
    }
}
