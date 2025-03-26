package com.bitc.fullstack405.securitytest.service;

import com.bitc.fullstack405.securitytest.database.dto.RegisterRequest;
import com.bitc.fullstack405.securitytest.database.dto.UserResponse;
import com.bitc.fullstack405.securitytest.database.dto.UserUpdateRequest;
import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import com.bitc.fullstack405.securitytest.utill.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 회원가입, 로그인, 토큰 발급 처리 서비스
@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final LogoutService logoutService;

  // 회원 등록
  public UserResponse register(RegisterRequest req){
    if(userRepository.existsByUsername(req.getUsername())){
      throw new IllegalArgumentException("Username is already in use");
    }
    UserEntity user = new UserEntity(
        null,
        req.getUsername(),
        passwordEncoder.encode(req.getPassword()),
        req.getNickname(),
        req.getDeveloper_yn()
    );
    UserEntity saveUser = userRepository.save(user);

    // 저장한 유저 데이터 리턴
    return UserResponse.registerUser(saveUser);
  }

  // 정보수정
  @Transactional
  public UserResponse updateUser(String username, UserUpdateRequest req) throws BadRequestException {

    // 현재 시큐리티에 저장된 유저 정보 가져오기
    String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity currentUser = userRepository.findByUsername(currentUserName);

    // 시큐리티 유저 id != 요청 유저 id
    if(!currentUser.getUsername().equals(username)){
      throw new BadRequestException("일치하는 회원이 없습니다.");
    }

//    UserEntity currentUser = userRepository.findById(id).orElse(null);
    // 같으면 업데이트

    // null 아닐 경우에만 업데이트
    if (req.getNickname() != null) currentUser.setNickname(req.getNickname());
    if (req.getPassword() != null) currentUser.setPassword(passwordEncoder.encode(req.getPassword()));
    if (req.getDeveloper_yn() != null) currentUser.setDeveloper_yn(req.getDeveloper_yn());

    // 업데이트한 값 바로 반영
    userRepository.saveAndFlush(currentUser);

    return UserResponse.updateUser(currentUser);
  }


  // 회원탈퇴
  @Transactional
  public ResponseEntity<String> deleteUser(String username, String accessToken, HttpServletResponse response) {
    // 유저정보 확인
    UserEntity user = userRepository.findByUsername(username);

    // 액세스 토큰 블랙리스트 추가, 리프레스 토큰 쿠키 삭제
    logoutService.logout(accessToken,response);

    // db에서 유저 삭제
    userRepository.delete(user);

    return ResponseEntity.ok("회원 탈퇴 완료");
  }
}
