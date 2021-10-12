package io.kyberorg.yalsee.configuration;

import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.utils.EncryptionUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configures Spring Security, doing the following:
 * Replaces default configuration.
 * Allow to access all resources, we will control access manually instead
 */
@AllArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private EncryptionUtils encryptionUtils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userService).passwordEncoder(encryptionUtils.getPasswordEncoder());
    }

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
