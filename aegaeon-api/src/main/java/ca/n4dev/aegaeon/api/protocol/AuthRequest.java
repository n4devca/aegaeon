package ca.n4dev.aegaeon.api.protocol;

import java.util.Collections;
import java.util.List;

/**
 * AuthFlow.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 11 - 2018
 */
public class AuthRequest {

    private String responseTypeParam;

    private List<ResponseType> responseTypes;

    private String nonce;

    private String state;

    /**
     * Create an auth request.
     */
    public AuthRequest() {
        this("", null, null);
    }

    /**
     * Create an auth request.
     * @param pResponseTypeParam The requested response type.
     */
    public AuthRequest(String pResponseTypeParam) {
        this(pResponseTypeParam, null, null);
    }

    /**
     * Create an auth request.
     * @param pResponseTypeParam The requested response type.
     * @param pNonce The nonce param.
     */
    public AuthRequest(String pResponseTypeParam, String pNonce) {
        this(pResponseTypeParam, pNonce, null);
    }

    /**
     * Create an auth request.
     * @param pResponseTypeParam The requested response type.
     * @param pNonce The nonce param.
     * @param pState The client state.
     */
    public AuthRequest(String pResponseTypeParam, String pNonce, String pState) {
        responseTypeParam = pResponseTypeParam;
        nonce = pNonce;
        state = pState;
        responseTypes = Collections.unmodifiableList(ResponseType.of(pResponseTypeParam));
    }

    /**
     * @return the responseTypeParam
     */
    public String getResponseTypeParam() {
        return responseTypeParam;
    }

    /**
     * @param pResponseTypeParam the responseTypeParam to set
     */
    public void setResponseTypeParam(String pResponseTypeParam) {
        responseTypeParam = pResponseTypeParam;
    }

    /**
     * @return the responseTypes
     */
    public List<ResponseType> getResponseTypes() {
        return responseTypes;
    }

    /**
     * @return the nonce
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * @param pNonce the nonce to set
     */
    public void setNonce(String pNonce) {
        nonce = pNonce;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param pState the state to set
     */
    public void setState(String pState) {
        state = pState;
    }

    public boolean contains(ResponseType pResponseType) {
        if (this.responseTypeParam != null && pResponseType != null) {
            return this.responseTypeParam.contains(pResponseType.toString().toLowerCase());
        }

        return false;
    }
}
