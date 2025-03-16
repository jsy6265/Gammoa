package com.bitc.fullstack405.securitytest.utill;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
  private final String SECRET_KEY = "your-secret-key";
  private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

  // 토큰 생성
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  // 토큰에서 유저 정보 추출
  public String getUsernameFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
