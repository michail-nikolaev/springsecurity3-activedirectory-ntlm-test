package org.nkey.test.ntlm.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author m.nikolaev Date: 10.11.12 Time: 2:25
 */
public class NtmlAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        // TODO
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Arrays.asList(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return "ROLE_USER";
                    }
                });
            }

            @Override
            public Object getCredentials() {
                return authentication.getCredentials();
            }

            @Override
            public Object getDetails() {
                return authentication.getDetails();
            }

            @Override
            public Object getPrincipal() {
                return authentication.getPrincipal();
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }

            public String getDisplayName() {
                return authentication.getName();
            }

            @Override
            public String getName() {
                return authentication.getName();
            }
        };
    }


}
