package ca.n4dev.aegaeon.api.protocol;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * AuthFlow.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 11 - 2018
 */
public class AuthRequest extends ClientRequest {

    private String responseType;

    private List<ResponseType> responseTypes;

    private String nonce;

    private String scope;

    private String display;

    private String prompt;

    private String idTokenHint;

    private RequestMethod requestMethod;

    public AuthRequest() {
    }

    public AuthRequest(String pResponseType,
                       String pScope,
                       String pClientPublicId,
                       String pRedirectUri) {
        this(pResponseType,
             pScope,
             pClientPublicId,
             pRedirectUri,
             null, null, null, null, null, null);
    }

    public AuthRequest(String pResponseType,
                       String pScope,
                       String pClientPublicId,
                       String pRedirectUri,
                       String pState,
                       String pNonce,
                       String pDisplay,
                       String pPrompt,
                       String pIdTokenHint,
                       RequestMethod pRequestMethod) {

        setResponseType(pResponseType);
        setScope(pScope);
        setClientId(pClientPublicId);
        setRedirectUri(pRedirectUri);
        setState(pState);
        setNonce(pNonce);
        setDisplay(pDisplay);
        setPrompt(pPrompt);
        setIdTokenHint(pIdTokenHint);
        setRequestMethod(pRequestMethod);
    }


    /**
     * @return the responseType
     */
    public String getResponseType() {
        return responseType;
    }

    /**
     * @param pResponseType the responseType to set
     */
    public void setResponseType(String pResponseType) {
        responseType = pResponseType;
        responseTypes = Collections.unmodifiableList(ResponseType.of(pResponseType));
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
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param pScope the scope to set
     */
    public void setScope(String pScope) {
        scope = pScope;
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @param pDisplay the display to set
     */
    public void setDisplay(String pDisplay) {
        display = pDisplay;
    }

    /**
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @param pPrompt the prompt to set
     */
    public void setPrompt(String pPrompt) {
        prompt = pPrompt;
    }

    /**
     * @return The prompt value as Enum.
     */
    public Prompt getPromptType() {
        return Prompt.from(prompt);
    }

    /**
     * @return the idTokenHint
     */
    public String getIdTokenHint() {
        return idTokenHint;
    }

    /**
     * @param pIdTokenHint the idTokenHint to set
     */
    public void setIdTokenHint(String pIdTokenHint) {
        idTokenHint = pIdTokenHint;
    }

    /**
     * @return the requestMethod
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * @param pRequestMethod the requestMethod to set
     */
    public void setRequestMethod(RequestMethod pRequestMethod) {
        requestMethod = pRequestMethod;
    }
}
