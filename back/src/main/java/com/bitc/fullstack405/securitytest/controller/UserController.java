package com.bitc.fullstack405.securitytest.controller;

import com.bitc.fullstack405.securitytest.database.dto.UserResponse;
import com.bitc.fullstack405.securitytest.database.dto.UserUpdateRequest;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import com.bitc.fullstack405.securitytest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserRepository userRepository;
  private final UserService userService;

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest req) throws BadRequestException {
    UserResponse updateUser = userService.updateUser(id, req);
    return ResponseEntity.ok(updateUser);
  }

}
