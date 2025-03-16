package com.bitc.fullstack405.securitytest.database.dto;

import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
  private String password;
  private String nickname;
  private String developer_yn;

//  public void updateUser(UserEntity userEntity, PasswordEncoder passwordEncoder) {
//    // null 아닐 경우에만 업데이트
//    if(this.password != null && !this.password.isBlank()) {
//      userEntity.setPassword(passwordEncoder.encode(this.password));
//    }
//    if(this.nickname != null && !this.nickname.isBlank()) {
//      userEntity.setNickname(this.nickname);
//    }
//    if(this.developer_yn != null && !this.developer_yn.isBlank()) {
//      userEntity.setDeveloper_yn(this.developer_yn);
//    }
//  }
  
}
