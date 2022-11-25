package pm.axe.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Setting up HTTP Security for Spring. Without this Spring Boot makes every application page protected.
     * We don't need it.
     *
     * @param httpSecurity {@link HttpSecurity} bean
     * @return configured {@link  HttpSecurity} bean
     * @throws Exception if any occur
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable() //vaadin has its own
                .authorizeRequests()
                .antMatchers("/protected/path").authenticated() //protect some page
                .antMatchers("/**").permitAll();
        return httpSecurity.build();
    }

    /**
     * Disabling WebSecurity from SpringBoot. We don't use it.
     *
     * @return {@link WebSecurityCustomizer} bean with disabled web security.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().anyRequest();
    }

}
