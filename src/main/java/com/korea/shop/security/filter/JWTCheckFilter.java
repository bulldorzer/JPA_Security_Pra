package com.korea.shop.security.filter;


import com.korea.shop.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
// 토큰 검증 하는 클래스
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    /*
    * 토큰 검증 하지 않아도 되는 도메인 또는 플리플라이트 체크 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        log.info("==================<shouldNotFilter>==================");
        
        String path = request.getServletPath();
        
        // 로그인 요청, /api/items, OPTIONS 필터는 제외 - 플리플라이트 (GET,POST,PUT,DELETE) 신호일때에는 예외
        if (path.startsWith("/api/items/")|| path.startsWith("/api/members/login")){
            return true;
        }

        if (request.getMethod().equals("OPTIONS")){
            return true;
        }
        return false;
    }

    /* shouldNotFilter조건을 제외한 모든 토큰은 여기서 토큰 검증을 진행함 */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("==================<JWTCheckFilter>==================");

        String authHeaderStr = request.getHeader("Authorization");

        // "Bearer "로 시작하는지 확인 없으면 다음으로 넘김
        if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")){
            log.info("==================< NO JWT Found, skipping Filter >============================");
            filterChain.doFilter(request,response);
            return;
        }

        try {
            /*
            * Bearer 타입 accesstoken 실제 토큰 부분 추출
            * 토큰 추출 -> 사용자정보 -> MemberDetails변환 -> 인증 객체 생성 -> SecurityContextHolder에 저장*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
