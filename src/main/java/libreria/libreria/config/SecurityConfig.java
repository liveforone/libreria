package libreria.libreria.config;

import libreria.libreria.jwt.JwtAuthenticationFilter;
import libreria.libreria.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/",
                                "/user/signup",
                                "/user/login"
                        ).permitAll()
                        .requestMatchers(
                                "/admin/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/item/post"
                        ).hasRole("SELLER")
                        .anyRequest().authenticated()
                        .and()
                        .addFilterBefore(
                                new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class
                        )
                )
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .and()
                // 403 예외처리 핸들링
                .exceptionHandling()
                .accessDeniedPage("/user/prohibition");
        return http.build();
    }
}
