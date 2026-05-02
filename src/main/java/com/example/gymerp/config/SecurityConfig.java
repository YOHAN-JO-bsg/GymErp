package com.example.gymerp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private static final String[] SWAGGER = {
        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
        "/swagger-resources/**", "/webjars/**", "/configuration/ui", "/configuration/security",
        "/upload/**", "/v1/product/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // 🔹 CSRF 비활성화 (테스트용)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 🔹 React CORS 설정
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers(SWAGGER).permitAll() // Swagger 허용
                .requestMatchers("/upload/**").permitAll()
                    .requestMatchers(
                            "/v1/emp/**", "/api/v1/emp/**",
                            "/v1/member/**", "/api/v1/member/**",
                            "/v1/sales/**", "/api/v1/sales/**",
                            "/v1/voucher/**", "/api/v1/voucher/**",
                            "/v1/pt/**", "/api/v1/pt/**",
                            "/v1/performance/**", "/api/v1/performance/**",
                            "/v1/home/**", "/api/v1/home/**"
                    ).permitAll()

                .requestMatchers("/v1/pt/**").permitAll()     // Swagger 테스트용 PT API 허용
                .requestMatchers("/v1/schedule/**").permitAll() // 일정 관련 API Swagger 테스트 허용
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.disable()) // 🔹 폼 로그인 비활성화
            .httpBasic(basic -> basic.disable()); // 🔹 기본 로그인 비활성화

        return http.build();
    }

    // BCryptPasswordEncoder 등록
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http,
                                               PasswordEncoder encoder,   // ← 여기만 PasswordEncoder로
                                               UserDetailsService service) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(service)
                   .passwordEncoder(encoder)
                   .and()
                   .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:9000",
                "http://yoman.store",
                "https://yoman.store",
                "http://3.36.225.219" // 🌟 요한님의 EC2 공인 IP도 명시적으로 추가
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        config.setExposedHeaders(List.of("Set-Cookie", "Authorization")); // 🌟 브라우저가 쿠키를 읽을 수 있게 노출
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}