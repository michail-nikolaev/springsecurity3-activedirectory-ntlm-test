package org.nkey.test.ntlm.filter;

import org.ntlmv2.liferay.NtlmManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * @author m.nikolaev Date: 20.11.12 Time: 2:30
 */
@Configuration
@PropertySource({ "classpath:activedirectory.properties" })
public class NtlmManagerConfiguration {
    public static final String NTLM_QUALIFIER = "ntlmContext";

    @Inject
    private Environment environment;

    @Bean
    public NtlmManager ntlmManager() {
        return new NtlmManager(environment.getProperty("ntlm.domain"),
                environment.getProperty("ntlm.domain.controller"),
                environment.getProperty("ntlm.domain.controller.host.name"),
                environment.getProperty("ntlm.service.computer.account"),
                environment.getProperty("ntlm.service.computer.account.password"));
    }

    @Bean
    @Qualifier(NTLM_QUALIFIER)
    public LdapContext ldapContext() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.SECURITY_AUTHENTICATION, environment.getProperty("active_directory.search.auth"));
        env.put(Context.SECURITY_PRINCIPAL, environment.getProperty("active_directory.search.account"));
        env.put(Context.SECURITY_CREDENTIALS, environment.getProperty("active_directory.search.account.password"));
        env.put(Context.PROVIDER_URL, environment.getProperty("active_directory.server"));
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }
}
