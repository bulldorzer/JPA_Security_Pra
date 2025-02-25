package com.korea.shop.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Log4j2
@Component // 컨테이너가 관리하는 클래스 : 의존성 주입이 가능해짐
public class JWTUtil {

    // 시크릿키 설정 (32bit 이상 필요), JWT 서명을 위한 비밀키
    private SecretKey key;

    public JWTUtil(@Value("${jwt.secret}") String secretKey) {
        log.info("=============<JWTUtill>=============");

        byte [] decodeKey = Base64.getDecoder().decode(secretKey);
        if (decodeKey.length<32){
            throw new IllegalArgumentException("JWT Secret Key키는 반드시 256비트 이상이여야합니다.");
        }
        // HMAC-SHA 알고리즘을 이용하여 비밀키 생성 -> UTF-8형식으로 변환
        this.key= Keys.hmacShaKeyFor(decodeKey);
    }

    // JWT 생성 메서드
    // valueMap : 사용자정보, min: 토큰 유효시간 => 이두가지가 claims로 바뀜
    // new HashMap<>(claims)를 사용하면 새로운 객체 생성으로 원본 claims로 분리(복사)되므로 예외 방지
    public String generateToken(Map<String, Object> valueMap, int min){
        try {
            /*
            * 토큰 객체 생성
            * 1) JWT API 를 통해 가져온 Jwts 객체
            * 2) 헤더 설정
            * 3) 사용자 정보 추가
            * 4) 발급시간 설정 IssuedAt = 발행
            * 5) 만료시간 설정 Expiration = 만료
            * 6) SecretKey 사용 */
            String jwtStr = Jwts.builder()
                    .setHeader(Map.of("typ", "JWT"))
                    .setClaims(valueMap)
                    .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                    .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                    .compact();
            return jwtStr;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /*
    * 토큰 유효성 검사 메서드
    * 파싱 : 데이터를 분석하고 분해해서 원하는 형태로 변환하는 과정
    * 반환형 : Claims로 변경 (JWT 라이브러리는 Claims 객체를 반환함)*/
    public Map<String, Object> validateToken(String token){
        try {
            /*
            * 1) 분해해서 검색
            * 2) 서명 검증 SecretKey 직접 사용
            * 3) 파싱 및 검증, 실패시 에허
            */
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (MalformedJwtException e) { // jwt 형식이 올바르지 않음
            throw new CustomJWTException("MalFored");
        }catch (ExpiredJwtException e) {
            throw new CustomJWTException("Expried"); // 토큰 시간이 만료됨
        }catch (InvalidClaimException e) {
            throw new CustomJWTException("Invaild"); // 클레임 정보가 올바르지 않음
        }catch (JwtException e) {
            throw new CustomJWTException("JWTError"); // 토큰 관련 예러
        }catch (Exception e) {
            throw new CustomJWTException("Error"); // 기타 알수 없는 에러
        }
    }
}
