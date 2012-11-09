package org.nkey.test.ntlm.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

/**
 * @author m.nikolaev Date: 09.11.12 Time: 23:01
 */
public class ActiveDirectoryLdapUserDetailsMapper extends LdapUserDetailsMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryLdapUserDetailsMapper.class);

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        UserDetails userDetails = super.mapUserFromContext(ctx, username, authorities);
        return new ActiveDirectoryUserDetails((LdapUserDetailsImpl) userDetails, ctx);
    }
}
