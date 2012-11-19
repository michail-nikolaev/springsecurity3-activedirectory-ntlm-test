package org.nkey.test.ntlm.filter;

import org.nkey.test.ntlm.ldap.LdapPrinciple;
import org.ntlmv2.liferay.NtlmUserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author m.nikolaev Date: 10.11.12 Time: 2:25
 */

public class NtmlAuthenticationManager implements AuthenticationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NtmlAuthenticationManager.class);

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final NtlmUserAccount ntlmUserAccount = (NtlmUserAccount) authentication.getPrincipal();

        final GrantedAuthority grantedAuthority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_USER";
            }
        };
        final LdapPrinciple principle = new LdapPrinciple() {
            @Override
            public String getDisplayName() {
                return ntlmUserAccount.getUserName();
            }

            @Override
            public String getDn() {
                return "dn";
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Arrays.asList(grantedAuthority);
            }

            @Override
            public String getPassword() {
                return "";
            }

            @Override
            public String getUsername() {
                return ntlmUserAccount.getUserName();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        Authentication result = new PreAuthenticatedAuthenticationToken(principle, "N/A") {
            @Override
            public Collection<GrantedAuthority> getAuthorities() {
                return Arrays.asList(grantedAuthority);
            }

            @Override
            public String getName() {
                return principle.getUsername();
            }

            @Override
            public Object getDetails() {
                return principle.getDisplayName();
            }
        };
        result.setAuthenticated(true);
        return result;
    }


}
