package org.nkey.test.ntlm.filter;

import org.nkey.test.ntlm.ldap.ActiveDirectoryLdapUserDetailsMapper;
import org.nkey.test.ntlm.ldap.LdapPrinciple;
import org.ntlmv2.liferay.NtlmUserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author m.nikolaev Date: 10.11.12 Time: 2:25
 */
public class NtlmAuthenticationManager implements AuthenticationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NtlmAuthenticationManager.class);

    @Inject @Qualifier(NtlmManagerConfiguration.NTLM_QUALIFIER)
    private LdapContext ldapContext;

    @Inject
    private ActiveDirectoryLdapUserDetailsMapper activeDirectoryLdapUserDetailsMapper;
    @Inject
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    private String ldapSearchRootDn;
    private String principalBindSuffix;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final NtlmUserAccount ntlmUserAccount = (NtlmUserAccount) authentication.getPrincipal();

        try {
            DirContextOperations userData = searchForUser(ldapContext, ntlmUserAccount.getUserName());
            final LdapPrinciple principle = (LdapPrinciple) activeDirectoryLdapUserDetailsMapper
                    .mapUserFromContext(userData, ntlmUserAccount.getUserName(), loadUserAuthorities(userData));


            Authentication result = new PreAuthenticatedAuthenticationToken(principle, "N/A") {
                @Override
                public Collection<GrantedAuthority> getAuthorities() {
                    return new LinkedList<>(grantedAuthoritiesMapper.mapAuthorities(principle.getAuthorities()));
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

        } catch (NamingException e) {
            LOGGER.error("Error on LDAP search", e);
            throw new AuthenticationServiceException(e.getLocalizedMessage());
        }
    }

    private DirContextOperations searchForUser(DirContext ctx, String username) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String searchFilter = "(&(objectClass=user)(userPrincipalName={0}))";
        final String bindPrincipal = username + principalBindSuffix;


        try {
            return SpringSecurityLdapTemplate
                    .searchForSingleEntryInternal(ctx, searchControls, ldapSearchRootDn, searchFilter,
                            new Object[]{ bindPrincipal });
        } catch (IncorrectResultSizeDataAccessException incorrectResults) {
            if (incorrectResults.getActualSize() == 0) {
                UsernameNotFoundException userNameNotFoundException =
                        new UsernameNotFoundException("User " + username + " not found in directory.");
                userNameNotFoundException.initCause(incorrectResults);
                LOGGER.error("bad credentials", userNameNotFoundException);

                throw userNameNotFoundException;
            }
            throw incorrectResults;
        }
    }

    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData) {
        String[] groups = userData.getStringAttributes("memberOf");

        if (groups == null) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        ArrayList<GrantedAuthority> authorities = new ArrayList<>(groups.length);
        for (String group : groups) {
            authorities.add(new SimpleGrantedAuthority(new DistinguishedName(group).removeLast().getValue()));
        }

        return authorities;
    }


    public void setLdapSearchRootDn(String ldapSearchRootDn) {
        this.ldapSearchRootDn = ldapSearchRootDn;
    }

    public void setPrincipalBindSuffix(String principalBindSuffix) {
        this.principalBindSuffix = principalBindSuffix;
    }
}
