package com.bitc.fullstack405.securitytest.database.dto;

import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
  // 회원 가입 후 정보 리턴
  private Long id;
  private String username;
  private String nickname;
  private String developer_yn;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;


  public static UserResponse registerUser(UserEntity userEntity) {
    return UserResponse.builder()
        .id(userEntity.getId())
        .username(userEntity.getUsername())
        .nickname(userEntity.getNickname())
        .developer_yn(userEntity.getDeveloper_yn())
        .createDate(userEntity.getCreate_date())
        .updateDate(userEntity.getUpdate_date())
        .build();
  }

  public static UserResponse updateUser(UserEntity userEntity) {
    return UserResponse.builder()
        .username(userEntity.getUsername())
        .nickname(userEntity.getNickname())
        .updateDate(userEntity.getUpdate_date())
        .build();
  }
}
