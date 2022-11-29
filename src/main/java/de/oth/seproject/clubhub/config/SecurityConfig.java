package de.oth.seproject.clubhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // everyone can access these sites
        http.authorizeRequests()
                .antMatchers("/", "/login", "/registration", "/user/create").permitAll();

        // manage access to announcement sites
        http.authorizeRequests()
                .antMatchers("/announcement/**", "/announcements/add/**")
                .hasAuthority("TRAINER")
                .and()
                .authorizeRequests()
                .antMatchers("/announcements")
                .authenticated();

        // the user needs to be logged in to access any other site
        http.authorizeRequests()
                .anyRequest()
                .authenticated();

        // configure login
        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home");

        // configure logout
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
