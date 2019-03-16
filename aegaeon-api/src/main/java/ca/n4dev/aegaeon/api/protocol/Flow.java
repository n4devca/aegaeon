package ca.n4dev.aegaeon.api.protocol;

/**
 * Flow.java
 *
 * Available flow (response_type) on authorization endpoint.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 16 - 2019
 */
public enum Flow {

    authorization_code,
    implicit,
    //hybrid
    client_credentials;

    public static Flow from(String pFlowName) {
        if (pFlowName != null) {

            for (Flow flow : Flow.values()) {
                if (flow.toString().equalsIgnoreCase(pFlowName)) {
                    return flow;
                }
            }
        }

        return null;
    }
}
