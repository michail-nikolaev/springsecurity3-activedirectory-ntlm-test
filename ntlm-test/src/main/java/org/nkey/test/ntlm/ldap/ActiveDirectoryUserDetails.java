package org.nkey.test.ntlm.ldap;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

import javax.naming.NamingException;
import java.util.Collection;

/**
 * @author m.nikolaev Date: 09.11.12 Time: 23:03
 */
public class ActiveDirectoryUserDetails extends LdapUserDetailsImpl implements LdapPrinciple {
    private String username;
    private String dn;
    private String displayName;
    private Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;

    public ActiveDirectoryUserDetails(LdapUserDetailsImpl from, DirContextOperations ctx) {
        this.username = from.getUsername();
        this.dn = from.getDn();
        this.authorities = from.getAuthorities();
        try {
            this.displayName = ctx.getAttributes("").get("displayName").getAll().next().toString();
        } catch (NamingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getDn() {
        return dn;
    }


    @Override
    public String getUsername() {
        return username;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "ActiveDirectoryUserDetails{" +
                "authorities=" + authorities +
                ", username='" + username + '\'' +
                ", dn='" + dn + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return dn.hashCode();
    }
}
