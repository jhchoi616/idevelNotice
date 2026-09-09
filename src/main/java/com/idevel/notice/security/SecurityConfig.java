package com.idevel.notice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능
                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/signup",
                                "/board/free/**",
                                "/board/free",
                                "/notice/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // 로그인 필요
                        .requestMatchers(
                                "/board/question/**",
                                "/board/info/**",
                                "/board/write",
                                "/mypage/**"
                        ).authenticated()

                        // 관리자만 접근
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // 나머지는 로그인 필요
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .userDetailsService(customUserDetailsService)

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}