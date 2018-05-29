package ca.n4dev.aegaeon.api.exception;

/**
 * Severity.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Apr 30 - 2018
 */
public enum Severity {
    INFO, WARNING, DANGER;

    public static Severity from(String pSeverity) {
        if (pSeverity != null) {
            for (Severity s : values()) {
                if (s.toString().equalsIgnoreCase(pSeverity)) {
                    return s;
                }
            }
        }

        return WARNING;
    }
}
