package de.oth.seproject.clubhub.config;

import de.oth.seproject.clubhub.persistence.model.RoleType;
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
                .antMatchers("/", "/home", "/login", "/registration", "/user/create").permitAll();

        // manage access to announcement sites
        http.authorizeRequests()
                .antMatchers("/announcement/**", "/announcements/add/**")
                .hasAuthority(RoleType.TRAINER.name())
                .and()
                .authorizeRequests()
                .antMatchers("/announcements")
                .authenticated();

        // manage access to group sites
        http.authorizeRequests()
                .antMatchers("/group/add/**", "/groups/create/**")
                .hasAuthority(RoleType.TRAINER.name())
                .and()
                .authorizeRequests()
                .antMatchers("/group", "/groups")
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
                .logoutSuccessUrl("/login?logout");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
