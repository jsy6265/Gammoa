package com.bitc.fullstack405.securitytest.database.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@RedisHash(value = "blacklist")
public class BlackList {

  @Id
  private String accessToken;

  @TimeToLive(unit = TimeUnit.MILLISECONDS)
  private Long expiration;

  @Builder
  private BlackList(Integer id, String accessToken, Long expiration) {
    this.accessToken = accessToken;
    this.expiration = expiration;
  }

}
