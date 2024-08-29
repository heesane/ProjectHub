package com.project.hub.validator;

import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

  private final UserRepository userRepository;

  public User validateAndGetUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(
            () -> new NotFoundException(ExceptionCode.USER_NOT_FOUND)
        );
  }

  public void isUserExist(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException(ExceptionCode.USER_NOT_FOUND);
    }
  }
}
