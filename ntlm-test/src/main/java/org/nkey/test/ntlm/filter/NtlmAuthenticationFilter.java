package org.nkey.test.ntlm.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jcifs.util.Base64;
import org.ntlmv2.liferay.NtlmLogonException;
import org.ntlmv2.liferay.NtlmManager;
import org.ntlmv2.liferay.NtlmUserAccount;
import org.ntlmv2.liferay.util.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author m.nikolaev Date: 10.11.12 Time: 1:33
 */
public class NtlmAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NtlmAuthenticationFilter.class);
    public static final String NTLM_HEADER_SENT = "ntlmHeaderSent";
    public static final String NTLM_AUTH_PREFIX = "NTLM";
    private static final String POST = "post";
    private Cache<String, byte[]> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    private SecureRandom secureRandom = new SecureRandom();

    @Inject
    private NtlmManager ntlmManager;

    private ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();


    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            responseThreadLocal.set((HttpServletResponse) response);
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            if (httpServletRequest.getMethod().toLowerCase().equals(POST)
                    && httpServletRequest.getContentLength() == 0) {
                getPreAuthenticatedPrincipal(httpServletRequest);
            } else {
                super.doFilter(request, response, chain);
            }
        } finally {
            responseThreadLocal.set(null);
        }
    }


    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(true);
        String authorization = getAuthorisation(request);
        HttpServletResponse httpServletResponse = responseThreadLocal.get();

        // if there is NTLM key in Authorization header
        if (authorization != null && authorization.startsWith(NTLM_AUTH_PREFIX)) {
            byte[] src = Base64.decode(authorization.substring(NTLM_AUTH_PREFIX.length() + 1));

            if (src[8] == 1) {
                LOGGER.debug("Create server challenge...");

                byte[] serverChallenge = new byte[8];
                secureRandom.nextBytes(serverChallenge);

                try {
                    byte[] challengeMessage = ntlmManager.negotiate(src, serverChallenge);
                    String authorizationMessages = Base64.encode(challengeMessage);

                    // send auth challenge in header to client
                    httpServletResponse.setContentLength(0);
                    httpServletResponse
                            .setHeader(HttpHeaders.WWW_AUTHENTICATE, NTLM_AUTH_PREFIX + " " + authorizationMessages);
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.flushBuffer();

                    // save serverChallenge for next request
                    cache.put(request.getRemoteAddr(), serverChallenge);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
                return null;
            } else {
                byte[] serverChallenge;
                LOGGER.trace("Get cached server challenge for: {}", request.getRemoteAddr());

                serverChallenge = cache.getIfPresent(request.getRemoteAddr());

                if (serverChallenge == null) {
                    LOGGER.debug("Start NTLM login...");
                    sendWwwAuthenticateResponse(httpServletResponse, httpSession);
                    return null;
                }

                NtlmUserAccount ntlmUserAccount;

                LOGGER.debug("Try authenticating user now...");
                try {
                    ntlmUserAccount = ntlmManager.authenticate(src, serverChallenge);
                } catch (IOException | NoSuchAlgorithmException | NtlmLogonException e) {
                    LOGGER.info("Auth fauiled: ", e);
                    return null;
                }
                if (ntlmUserAccount != null) {
                    LOGGER.info("Authentication was successful.");
                }
                return ntlmUserAccount;
            }
        } else {
            sendWwwAuthenticateResponse(httpServletResponse, httpSession);
        }
        return null;
    }


    private void sendWwwAuthenticateResponse(HttpServletResponse response, HttpSession httpSession) {
        // we are asking for NTLM only once
        if (httpSession.getAttribute(NTLM_HEADER_SENT) == null) {
            response.setContentLength(0);
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, NTLM_AUTH_PREFIX);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                response.flushBuffer();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            httpSession.setAttribute(NTLM_HEADER_SENT, true);
        }
    }

    public String getAuthorisation(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(NTLM_AUTH_PREFIX)) {
            return authorization;
        }
        return null;
    }
}
