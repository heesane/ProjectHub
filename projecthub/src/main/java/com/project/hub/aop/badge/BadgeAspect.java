package com.project.hub.aop.badge;

import com.project.hub.entity.Badge;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.repository.jpa.BadgeRepository;
import com.project.hub.repository.jpa.CommentsLikeRepository;
import com.project.hub.repository.jpa.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class BadgeAspect {

  private final UserRepository userRepository;
  private final BadgeRepository badgeRepository;
  private final CommentsLikeRepository commentsLikeRepository;

  @Around("@annotation(badgeCheck) && args(badgeInterface)")
  public Object around(
      ProceedingJoinPoint pjp,
      BadgeCheck badgeCheck,
      BadgeInterface badgeInterface
  ) {
    try {

      // 먼저 Annotation이 붙은 메서드 실행 후 처리
      Object proceed = pjp.proceed();

      Long userId = badgeInterface.getId();

      User badgeUser = userRepository.findById(userId).orElseThrow(
          () -> new NotFoundException(ExceptionCode.USER_NOT_FOUND)
      );

      Long userProjectCount = (long) badgeUser.getProjects().size();
      Long userCommentCount = commentsLikeRepository.countByUserId(userId);

      List<Badge> badgeList = badgeRepository.findAll();

      Badge userBadge = null;

      for (Badge badge : badgeList) {
        Long requiredProjectCount = badge.getRequiredProjectCount();
        Long requiredCommentCount = badge.getRequiredCommentCount();

        if (userProjectCount >= requiredProjectCount && userCommentCount >= requiredCommentCount) {
          userBadge = badge;
        }
      }

      if(userBadge != null && badgeUser.getBadge() != userBadge) {
        badgeUser.updateBadge(userBadge);
      }
      userRepository.saveAndFlush(badgeUser);
      return proceed;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

  }

}
