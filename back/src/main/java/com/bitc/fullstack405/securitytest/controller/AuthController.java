package com.bitc.fullstack405.securitytest.controller;

import com.bitc.fullstack405.securitytest.database.dto.AuthRequest;
import com.bitc.fullstack405.securitytest.database.dto.AuthResponse;
import com.bitc.fullstack405.securitytest.database.dto.RegisterRequest;
import com.bitc.fullstack405.securitytest.database.dto.UserResponse;
import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import com.bitc.fullstack405.securitytest.service.AuthService;
import com.bitc.fullstack405.securitytest.utill.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;

  
  // 로그인
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    try{
    Authentication authentication = authenticationManager.authenticate(
        // 사용자 데이터 시큐리티에서 검증하기 위해 토큰 생성해서 authenticationManager 한테 보냄
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    // 있으면 컨텍스트에 저장
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 성공 시 토큰 + 유저 id 리턴
    UserEntity user = userRepository.findByUsername(request.getUsername());
    String token = jwtProvider.generateToken(user.getUsername());

    return ResponseEntity.ok(new AuthResponse(token,user.getUsername()));
    }catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new AuthResponse(null, "Invalid username or password"));
    }
  }

  
  // 회원가입
  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest req) {
    UserResponse res = authService.register(req);
    return ResponseEntity.ok(res);
  }

}
