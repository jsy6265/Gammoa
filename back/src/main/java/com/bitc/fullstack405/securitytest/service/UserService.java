package com.bitc.fullstack405.securitytest.service;

import com.bitc.fullstack405.securitytest.database.dto.UserResponse;
import com.bitc.fullstack405.securitytest.database.dto.UserUpdateRequest;
import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponse updateUser(Long id, UserUpdateRequest req) throws BadRequestException {

    // 현재 시큐리티에 저장된 유저 정보 가져오기
    String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity currentUser = userRepository.findByUsername(currentUserName);

    // 시큐리티 유저 id != 요청 유저 id
    if(!currentUser.getId().equals(id)){
      throw new BadRequestException("Username does not match");
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


}
