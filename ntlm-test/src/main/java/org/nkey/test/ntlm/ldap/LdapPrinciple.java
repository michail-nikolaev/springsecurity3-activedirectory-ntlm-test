package org.nkey.test.ntlm.ldap;

import org.springframework.security.ldap.userdetails.LdapUserDetails;

/**
 * @author m.nikolaev Date: 16.11.12 Time: 4:20
 */
public interface LdapPrinciple extends LdapUserDetails {
    String getDisplayName();
}
