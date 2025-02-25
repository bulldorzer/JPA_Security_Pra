package com.korea.shop.Config;


import com.korea.shop.security.filter.JWTCheckFilter;
import com.korea.shop.security.handler.CustomAccessDeniedHandler;
import com.korea.shop.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/* 보안 설정 클래스 */
@Configuration // 설정 클래스 정의
@RequiredArgsConstructor // 필드 포함된 생성자 자동 생성
@Log4j2 // 롬북 API를 이용한 로그기능 추가
@EnableMethodSecurity // 메서드 보안 정밀 기능 = 접근제어
public class SecurityConfig {

    // 인증 매니저 등록
    @Bean // 컨테이너에 빈 등록함으로서 객체 application 영역에서 어디든 사용가능
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager(); // 인증 매니저 객체 반환
    }

    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("========<Security Config>========");
        
        /*
        * 메서드 체인으로 연결하여 HttpSecurity 객체 메서드 체인으로 여러 설정 할것
        * 1) cors설정 : 외부 도메인 접근허용
        * 2) session 사용 여부 설정 (사용안함)
        * 3) url 접근 제어 허용 가능한 url 등록
        * 4) JWT 필터  설정
        * 5) csrf 비활성화 => 세션 사용 안하기 떄문
        * 6) 로그인 설정 (폼 로그인 & HTTP Basic 인증 비활성화 => JWT 기반 로그인 사용)
        * 7) 예외 핸들러 (필터 적용후 설정)
        */

        http.cors( httpSecurityCorsConfigurer
                -> httpSecurityCorsConfigurer.configurationSource(configurationSource()))
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(atuhz -> {
                    atuhz
                            .requestMatchers("api/members/login").permitAll() // 로그인 화면
                            .requestMatchers("api/items/**").permitAll() // 상품
                            .requestMatchers("/api/orders/**").hasRole("USER") // 주문목록
                            .requestMatchers("/api/deliveries/**").hasRole("USER") // 배달목록
                            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN,MANAGER") // 관리자 화면
                            .anyRequest().authenticated(); // 다른 요청은 인증 필요
                })
                .addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .csrf(config -> config.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .exceptionHandling(config -> config.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    // cors 설정 내용
    @Bean
    public CorsConfigurationSource configurationSource (){
        /*
        * 1) 허용할 도메일, 출저를 작성
        * 2) 도메인에서 허용할 메서드 설정
        * 3) 허용할 헤더 내용
        * 4) 자격 증명 허용
        * 5) cors 설정 적용한 url pattern 지정하는 객체 생성
        */
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","GET","POST","PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        // cors 설정 적용한 url pattern 지정하는 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 패스워드 인코더
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

}
