package ca.n4dev.aegaeon.api.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * ResponseType.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 11 - 2018
 */
public enum ResponseType {
    ID_TOKEN, CODE, TOKEN;

    /**
     * Get a ResponseType from a String
     * @param pResponseType The ResponseType as String
     * @return A ResponseType or null.
     */
    public static ResponseType from(String pResponseType) {
        for (ResponseType rt : ResponseType.values()) {
            if (rt.toString().equalsIgnoreCase(pResponseType)) {
                return rt;
            }
        }
        return null;
    }

    /**
     * Get many ResponseTypes from a String. Value are split by space
     * @param pResponseTypeStr The ResponseType as String
     * @return One or many ResponseType or empty.
     */
    public static List<ResponseType> of(String pResponseTypeStr) {
        List<ResponseType> responseTypes = new ArrayList<>();

        if (pResponseTypeStr != null && !pResponseTypeStr.isEmpty()) {
            String[] args = pResponseTypeStr.split(" ");

            for (String r : args) {
                for (ResponseType rt : ResponseType.values()) {
                    if (rt.toString().equalsIgnoreCase(r)) {
                        responseTypes.add(rt);
                        break;
                    }
                }
            }
        }

        return responseTypes;
    }
}
