package com.matthewcasperson.onenote.configuration;

import com.azure.spring.aad.webapp.AADWebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthSecurityConfig extends AADWebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        super.configure(http);
        // @formatter:off
        http
            .authorizeRequests()
                .antMatchers("/", "/login", "/*.js", "/*.css").permitAll()
                .anyRequest().authenticated()
            .and()
                .csrf()
                .disable();
        // @formatter:on
    }
}
