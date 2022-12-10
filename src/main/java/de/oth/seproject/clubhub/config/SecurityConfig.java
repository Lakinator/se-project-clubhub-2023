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

        // TODO: make rest api available without authentication

        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/announcement/**", "/announcements/add/**", "/group/add/**", "/groups/create/**")
                .hasAuthority(RoleType.TRAINER.name())
                .antMatchers("/", "/home", "/login", "/registration", "/user/create", "/webjars/**", "/js/**", "/css/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
