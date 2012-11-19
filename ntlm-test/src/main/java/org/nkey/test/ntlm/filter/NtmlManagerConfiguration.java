package org.nkey.test.ntlm.filter;

import org.ntlmv2.liferay.NtlmManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

/**
 * @author m.nikolaev Date: 20.11.12 Time: 2:30
 */
@Configuration
@PropertySource({ "classpath:activedirectory.properties" })
public class NtmlManagerConfiguration {

    @Inject
    private Environment environment;

    @Bean
    public NtlmManager ntlmManager() {
        return new NtlmManager(environment.getProperty("ntlm.domain"), environment.getProperty("ntlm.domainController"),
                environment.getProperty("ntlm.domainControllerHostName"),
                environment.getProperty("ntlm.serviceAccount"), environment.getProperty("ntlm.servicePassword"));
    }
}
