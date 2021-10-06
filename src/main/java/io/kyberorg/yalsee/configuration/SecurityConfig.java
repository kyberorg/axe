package io.kyberorg.yalsee.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configures Spring Security, doing the following:
 * Replaces default configuration.
 * Allow to access all resources, we will control access manually instead
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() //vaadin has own
                .authorizeRequests()
                .antMatchers("/protected/path").authenticated() //protect some page
                .antMatchers("/**").permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().anyRequest();
    }
}
