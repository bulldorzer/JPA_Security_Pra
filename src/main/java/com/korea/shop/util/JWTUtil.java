package com.korea.shop.util;

import io.jsonwebtoken.Jwts;
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
}
