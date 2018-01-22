package ca.n4dev.aegaeon.server.security;

import ca.n4dev.aegaeon.api.validation.PasswordEvaluator;
import ca.n4dev.aegaeon.api.validation.PasswordValidity;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * NormalPasswordEvaluator.java
 *
 * A password evaluator with "normal rules".
 *
 * 1. Must be at least 8 characters long
 * 2. An upper case letter
 * 3. A lower case letter
 * 4. A number
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 18 - 2018
 */
@Component
public class NormalPasswordEvaluator implements PasswordEvaluator {

    private static final List<String> BANNED_WORLD;
    private static final Pattern patternLowerCase = Pattern.compile("[a-z]+");
    private static final Pattern patternUpperCase = Pattern.compile("[A-Z]+");
    private static final Pattern patternNumber = Pattern.compile("[0-9]+");
    private static final Pattern patternElse = Pattern.compile("[^0-9A-Za-z]+");

    static {
        List<String> bad = new ArrayList<>();

        bad.add("password");
        bad.add("qwerty");
        bad.add("12345");
        bad.add("letmein");
        bad.add("football");
        bad.add("iloveyou");
        bad.add("admin");
        bad.add("welcome");
        bad.add("monkey");
        bad.add("login");
        bad.add("abc123");
        bad.add("starwars");
        bad.add("123123");
        bad.add("dragon");
        bad.add("passw0rd");
        bad.add("maste");
        bad.add("hello");
        bad.add("freedom");
        bad.add("whatever");
        bad.add("qazwsx");
        bad.add("trustno1");
        bad.add("allo");

        BANNED_WORLD = Collections.unmodifiableList(bad);
    }


    @Override
    public PasswordValidity evaluate(String pPassword) {

        if (Utils.isEmpty(pPassword)) {
            return invalid("passwd.evaluation.empty");
        }

        if (pPassword.length() < 8) {
            return invalid("passwd.evaluation.tooshort");
        }

        if (BANNED_WORLD.stream().anyMatch(bad -> pPassword.toLowerCase().contains(bad))) {
            return invalid("passwd.evaluation.badpractice");
        }

        if (!patternUpperCase.matcher(pPassword).find()) {
            return invalid("passwd.evaluation.nouppercase");
        }

        if (!patternLowerCase.matcher(pPassword).find()) {
            return invalid("passwd.evaluation.nolowercase");
        }

        if (!patternNumber.matcher(pPassword).find() && !patternElse.matcher(pPassword).find()) {
            return invalid("passwd.evaluation.nonumber");
        }

        return valid();
    }

    private PasswordValidity valid() {
        return new PasswordValidityImpl(true, null, 50);
    }

    private PasswordValidity invalid(String pReason) {
        return new PasswordValidityImpl(false, pReason, 0);
    }

    private static final class PasswordValidityImpl implements PasswordValidity {

        private boolean valid;
        private String reason;
        private int strength;

        private PasswordValidityImpl(boolean pValid, String pReason, int pStrength) {
            this.valid = pValid;
            this.reason = pReason;
            this.strength = pStrength;
        }

        @Override
        public boolean isValid() {
            return this.valid;
        }

        @Override
        public String getReason() {
            return this.reason;
        }

        @Override
        public int getStrength() {
            return this.strength;
        }
    }
}
