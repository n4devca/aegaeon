package ca.n4dev.aegaeon.api.protocol;

/**
 * TokenRequest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class TokenRequest extends AuthRequest {

    private String grantType;
    private String code;
    private String refreshToken;

    public TokenRequest() {
    }

    public TokenRequest(String pGrantType,
                        String pCode,
                        String pClientId,
                        String pRedirectUri,
                        String pScope,
                        String pRefreshToken) {

        setGrantType(pGrantType);
        setCode(pCode);
        setClientId(pClientId);
        setRedirectUri(pRedirectUri);
        setScope(pScope);
        setRefreshToken(pRefreshToken);

    }

    public TokenRequest(AuthRequest pAuthRequest) {

        setResponseType(pAuthRequest.getResponseType());
        setScope(pAuthRequest.getScope());
        setClientId(pAuthRequest.getClientId());
        setRedirectUri(pAuthRequest.getRedirectUri());
        setState(pAuthRequest.getState());
        setNonce(pAuthRequest.getNonce());
        setDisplay(pAuthRequest.getDisplay());
        setPrompt(pAuthRequest.getPrompt());
        setIdTokenHint(pAuthRequest.getIdTokenHint());
        setRequestMethod(pAuthRequest.getRequestMethod());

    }

    public static TokenRequest of(String pGrantType,
                                  String pCode,
                                  String pClientId,
                                  String pRedirectUri,
                                  String pScope,
                                  String pRefreshToken) {
        TokenRequest tokenRequest = new TokenRequest();

        tokenRequest.setGrantType(pGrantType);
        tokenRequest.setCode(pCode);
        tokenRequest.setClientId(pClientId);
        tokenRequest.setRedirectUri(pRedirectUri);
        tokenRequest.setScope(pScope);
        tokenRequest.setRefreshToken(pRefreshToken);

        return tokenRequest;
    }

    /**
     * @return the grantType
     */
    public String getGrantType() {
        return grantType;
    }

    /**
     * @param pGrantType the grantType to set
     */
    public void setGrantType(String pGrantType) {
        grantType = pGrantType;
    }

    /**
     * @return the grantType
     */
    public GrantType getGrantTypeAsType() {
        return GrantType.from(grantType);
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param pCode the code to set
     */
    public void setCode(String pCode) {
        code = pCode;
    }

    /**
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param pRefreshToken the refreshToken to set
     */
    public void setRefreshToken(String pRefreshToken) {
        refreshToken = pRefreshToken;
    }

    @Override
    public String toString() {
        return "TokenRequest{" +
                "grantType='" + grantType + '\'' +
                ", clientId='" + clientId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                '}';
    }
}
