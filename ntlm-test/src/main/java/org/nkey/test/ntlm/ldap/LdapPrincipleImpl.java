package org.nkey.test.ntlm.ldap;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

import javax.annotation.Generated;
import javax.naming.NamingException;
import java.util.Collection;

/**
 * @author m.nikolaev Date: 20.11.12 Time: 1:42
 */
public class LdapPrincipleImpl implements LdapPrinciple {
    private String username;
    private String dn;
    private String displayName;
    private Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
    private String password;

    public LdapPrincipleImpl(LdapUserDetailsImpl from, DirContextOperations ctx) {
        this.username = from.getUsername();
        this.dn = from.getDn();
        this.authorities = from.getAuthorities();
        this.password = StringUtils.EMPTY;
        try {
            this.displayName = ctx.getAttributes("").get("displayName").getAll().next().toString();
        } catch (NamingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDn() {
        return dn;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @Generated("idea")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LdapPrincipleImpl that = (LdapPrincipleImpl) o;

        if (authorities != null ? !authorities.equals(that.authorities) : that.authorities != null) {
            return false;
        }
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) {
            return false;
        }
        if (dn != null ? !dn.equals(that.dn) : that.dn != null) {
            return false;
        }
        if (password != null ? !password.equals(that.password) : that.password != null) {
            return false;
        }
        if (username != null ? !username.equals(that.username) : that.username != null) {
            return false;
        }

        return true;
    }

    @Override
    @Generated("idea")
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (dn != null ? dn.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
