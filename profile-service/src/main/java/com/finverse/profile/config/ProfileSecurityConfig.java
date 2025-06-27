package com.finverse.profile.config;

//import com.finverse.profile.filter.JwtAuthenticationFilter;
//import com.finverse.security.user.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ProfileSecurityConfig {

//    private final JwtTokenUtil jwtTokenUtil;
//
//    public ProfileSecurityConfig(JwtTokenUtil jwtTokenUtil) {
//        this.jwtTokenUtil = jwtTokenUtil;
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
//                .antMatchers("/profile/**").authenticated()
                .anyRequest().permitAll();
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
