package ca.n4dev.aegaeon.server.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import ca.n4dev.aegaeon.api.protocol.FlowUtils;

/**
 * RequestBuilder.java
 * <p>
 * A fluent object to build authorization request.
 *
 * @author rguillemette
 * @since 2.0.0 - May 10 - 2018
 */
public class RequestBuilder {

    private String responseType;
    private String state;
    private String nonce;
    private String clientId;
    private String redirectionUrl;
    private List<String> scopes;
    private String display;
    private String prompt;
    private String idTokenHint;

    public RequestBuilder() {
        scopes = new ArrayList<>();
    }

    public static RequestBuilder authorizationCode() {
        RequestBuilder b = new RequestBuilder();
        b.responseType(FlowUtils.RTYPE_AUTH_CODE);
        return b;
    }

    public static RequestBuilder implicit() {
        RequestBuilder b = new RequestBuilder();
        b.responseType(FlowUtils.RTYPE_IMPLICIT_FULL);
        return b;
    }

    public RequestBuilder responseType(String pResponseType) {
        Assert.notEmpty(pResponseType);
        responseType = pResponseType;
        return this;
    }

    /**
     * Set a request state.
     *
     * @param pState The state
     * @return This builder.
     */
    public RequestBuilder state(String pState) {
        Assert.notEmpty(pState);
        state = pState;
        return this;
    }

    public RequestBuilder nonce(String pNonce) {
        Assert.notEmpty(pNonce);
        nonce = pNonce;
        return this;
    }

    public RequestBuilder clientId(String pClientId) {
        Assert.notEmpty(pClientId);
        clientId = pClientId;
        return this;
    }

    public RequestBuilder redirection(String pRedirectionUrl) {
        Assert.notEmpty(pRedirectionUrl);
        redirectionUrl = pRedirectionUrl;
        return this;
    }

    public RequestBuilder scopes(String... pScope) {
        Stream.of(pScope).forEach(scopes::add);
        return this;
    }

    public RequestBuilder display(String pDisplay) {
        Assert.notEmpty(pDisplay);
        display = pDisplay;
        return this;
    }

    public RequestBuilder prompt(String pPrompt) {
        Assert.notEmpty(pPrompt);
        prompt = pPrompt;
        return this;
    }

    public RequestBuilder idTokenHint(String pIdTokenHint) {
        Assert.notEmpty(pIdTokenHint);
        idTokenHint = pIdTokenHint;
        return this;
    }

    public Map<String, String> build() {
        Map<String, String> params = new LinkedHashMap<>();

        put(params, UriBuilder.PARAM_RESPONSE_TYPE, responseType);
        put(params, UriBuilder.PARAM_CLIENT_ID, clientId);
        put(params, UriBuilder.PARAM_REDIRECTION_URL, redirectionUrl);
        put(params, UriBuilder.PARAM_STATE, state);
        put(params, UriBuilder.PARAM_NONCE, nonce);
        put(params, UriBuilder.PARAM_SCOPE, String.join(" ", scopes));
        put(params, UriBuilder.PARAM_DISPLAY, display);
        put(params, UriBuilder.PARAM_PROMPT, prompt);
        put(params, UriBuilder.PARAM_IDTOKENHINT, idTokenHint);

        return params;
    }

    private static void put(Map<String, String> pParams, String pKey, String pValue) {
        if (Utils.isNotEmpty(pKey) && Utils.isNotEmpty(pValue)) {
            pParams.put(pKey, pValue);
        }
    }
}
