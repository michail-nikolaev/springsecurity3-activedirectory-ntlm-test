package org.nkey.test.ntlm.conf;

import org.nkey.test.ntlm.BasePackageMarker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author m.nikolaev Date: 09.11.12 Time: 20:01
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = BasePackageMarker.class)
@ImportResource({ "classpath*:*spring-security.xml"})
public class WebConfiguration {
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

}
