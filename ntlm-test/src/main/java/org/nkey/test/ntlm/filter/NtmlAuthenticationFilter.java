package org.nkey.test.ntlm.filter;

import com.sun.security.auth.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author m.nikolaev Date: 10.11.12 Time: 1:33
 */
public class NtmlAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NtmlAuthenticationFilter.class);

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        if (request.getParameterMap().containsKey("nkey")) {
            return new UserPrincipal("nkey");
        } else {
            return null;
        }
    }
}
