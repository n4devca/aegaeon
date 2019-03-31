package ca.n4dev.aegaeon.server.event;

import java.util.Collection;
import java.util.List;

import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import static ca.n4dev.aegaeon.server.utils.LogUtils.asString;
import static ca.n4dev.aegaeon.server.utils.LogUtils.join;

/**
 * LoggerEventListener.java
 * <p>
 * Catch events and log them.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 30 - 2019
 */
@Component
public class LoggerEventListener {

    private static final Logger AUTHENTICATION_LOGGER = LoggerFactory.getLogger(AuthenticationEvent.class);
    private static final Logger INTROSPECT_LOGGER = LoggerFactory.getLogger(IntrospectEvent.class);
    private static final Logger USER_INFO_LOGGER = LoggerFactory.getLogger(UserInfoEvent.class);
    private static final Logger TOKENGRANT_LOGGER = LoggerFactory.getLogger(TokenGrantEvent.class);

    @Async
    @EventListener
    public void logTokenGrantingEvent(TokenGrantEvent pTokenGrantEvent) {
        try {


            final String clientId = asString(pTokenGrantEvent.getClientId());
            final String grantType = asString(pTokenGrantEvent.getGrantType());
            final List<String> requestedScopes = Utils.explode(pTokenGrantEvent.getRequestedScope());
            final List<String> allowedScopes = Utils.explode(pTokenGrantEvent.getAllowedScope());
            final String requestedScopeStr = "[" + Utils.join(",", requestedScopes, pS -> pS) + "]";
            final String allowedScopeStr = "[" + Utils.join(",", allowedScopes, pS -> pS) + "]";
            final String userId = asString(pTokenGrantEvent.getUserId());

            final StringBuilder tokenInfoBuilder
                    = new StringBuilder()
                    .append("[")
                    .append(pTokenGrantEvent.isIdToken() ? "I" : "-").append(",")
                    .append(pTokenGrantEvent.isAccessToken() ? "A" : "-").append(",")
                    .append(pTokenGrantEvent.isRefreshToken() ? "R" : "-")
                    .append("]");

            final String tokens = tokenInfoBuilder.toString();

            MDC.put("clientId", clientId);
            MDC.put("grantType", grantType);
            MDC.put("requestedScope", requestedScopeStr);
            MDC.put("allowedScope", allowedScopeStr);
            MDC.put("userId", userId);
            MDC.put("tokens", tokens);

            TOKENGRANT_LOGGER.info(join(clientId, grantType, requestedScopeStr, allowedScopeStr, userId, tokens));

        } finally {
            MDC.clear();
        }
    }

    @Async
    @EventListener
    public void logIntrospectEvent(IntrospectEvent pIntrospectEvent) {
        try {

            final String clientId = asString(pIntrospectEvent.getClientId());
            final String clientAllowed = asString(pIntrospectEvent.isClientAllowed());
            final String valueReturned = asString(pIntrospectEvent.getValueReturned());
            final String userName = asString(pIntrospectEvent.getUserId());

            MDC.put("clientId", clientId);
            MDC.put("clientAllowed", clientAllowed);
            MDC.put("userId", userName);
            MDC.put("result", valueReturned);

            INTROSPECT_LOGGER.info(join(IntrospectEvent.class.getSimpleName(), clientId, clientAllowed, userName, valueReturned));

        } finally {
            MDC.clear();
        }
    }

    @Async
    @EventListener
    public void logUserInfoEvent(UserInfoEvent pUserInfoEvent) {
        try {

            final String clientId = asString(pUserInfoEvent.getClientId());
            final String scopes = "[" + asString(Utils.join(",", pUserInfoEvent.getScopes(), pS -> pS.getName())) + "]";
            final String userId = asString(pUserInfoEvent.getUserId());

            MDC.put("clientId", clientId);
            MDC.put("scopes", scopes);
            MDC.put("userId", userId);

            USER_INFO_LOGGER.info(join(clientId, scopes, userId));

        } finally {
            MDC.clear();
        }
    }

    @Async
    @EventListener
    public void logSuccessfulAuthenticationEvent(AuthenticationSuccessEvent pSuccessEvent) {

        final Authentication authentication = pSuccessEvent.getAuthentication();
        String userName = asString(getUserName(authentication.getPrincipal()));
        String roles = "[" + asString(getRoles(authentication.getAuthorities())) + "]";
        String ip = asString(getIpAddress(authentication.getDetails()));
        String sessionId = asString(getSessionId(authentication.getDetails()));
        final String status = "authenticated";

        try {

            MDC.put("userName", userName);
            MDC.put("roles", roles);
            MDC.put("sessionId", sessionId);
            MDC.put("ip", ip);
            MDC.put("status", status);

            AUTHENTICATION_LOGGER.info(join(userName, roles, ip, status));

        } finally {
            MDC.clear();
        }
    }

    @Async
    @EventListener(classes = {AuthenticationFailureBadCredentialsEvent.class,
            AuthenticationFailureDisabledEvent.class,
            AuthenticationFailureExpiredEvent.class,
            AuthenticationFailureLockedEvent.class,
            AuthenticationFailureCredentialsExpiredEvent.class,
            AuthenticationCredentialsNotFoundEvent.class})
    public void logFailureAuthenticationEvent(AbstractAuthenticationFailureEvent pFailureEvent) {
        try {

            final Object source = pFailureEvent.getSource();
            Object details = null;

            if (source instanceof UsernamePasswordAuthenticationToken) {
                final UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) source;
                details = authenticationToken.getDetails();
            }

            final String userName = asString(getUserName(pFailureEvent.getAuthentication().getPrincipal()));
            final String roles = "[" + asString(getRoles(pFailureEvent.getAuthentication().getAuthorities())) + "]";
            final String ip = asString(getIpAddress(details));
            final String status = pFailureEvent.getClass().getSimpleName();

            MDC.put("userName", userName);
            MDC.put("roles", roles);
            MDC.put("sessionId", "-");
            MDC.put("ip", ip);
            MDC.put("status", status);

            AUTHENTICATION_LOGGER.info(join(userName, roles, ip, status));

        } finally {
            MDC.clear();
        }
    }


    private String getUserName(Object pPrincipal) {

        if (pPrincipal instanceof String) {
            return (String) pPrincipal;
        } else if (pPrincipal instanceof AegaeonUserDetails) {
            return ((AegaeonUserDetails) pPrincipal).getUsername();
        }

        return null;
    }

    private String getIpAddress(Object pDetails) {

        if (pDetails instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) pDetails).getRemoteAddress();
        }

        return null;
    }

    private String getSessionId(Object pDetails) {

        if (pDetails instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) pDetails).getSessionId();
        }

        return null;
    }

    private String getRoles(Object pAuthorities) {

        if (pAuthorities instanceof Collection) {
            Collection<?> authorities = (Collection<?>) pAuthorities;
            if (Utils.isEmpty(authorities)) {
                return "NONE";
            } else {
                return Utils.join(",", authorities, pO -> pO.toString());
            }

        } else if (pAuthorities instanceof String) {
            return (String) pAuthorities;
        }

        return null;
    }

}
