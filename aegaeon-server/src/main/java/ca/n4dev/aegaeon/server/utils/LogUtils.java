package ca.n4dev.aegaeon.server.utils;

/**
 * LogUtils.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 27 - 2019
 */
public class LogUtils {

    private static final String DASH = "-";

    private LogUtils() {
    }

    public static String asString(Object pObject) {

        if (pObject != null) {

            if (pObject instanceof String) {
                return (String) pObject;
            } else {
                return String.valueOf(pObject);
            }
        }

        return DASH;
    }

    public static String join(String... pStrings) {
        if (pStrings != null) {
            return String.join(" ", pStrings);
        }

        return "";
    }
}
