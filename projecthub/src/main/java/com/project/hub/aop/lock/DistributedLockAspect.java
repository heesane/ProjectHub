package com.project.hub.aop.lock;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

  private final RedissonClient redissonClient;

  @Around("@annotation(distributedLock) && args(entity)")
  public Object around(
      ProceedingJoinPoint pjp,
      DistributedLock distributedLock,
      DistributedLockInterface entity
      ) throws Throwable {

    long waitTime = distributedLock.waitTime();
    long leaseTime = distributedLock.leaseTime();

    String entityId = entity.getId().toString();
    String entityType = entity.getEntityType();
    String lockKey = entityType + ":" + entityId;

    RLock lock = redissonClient.getLock(lockKey);

    boolean isLocked = false;
    try {
      isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
      if (isLocked) {
        return pjp.proceed();  // 실제 메소드 실행
      } else {
        throw new RuntimeException("Could not acquire the lock - " + lockKey);
      }
    } finally {
      if (isLocked) {
        lock.unlock();  // 메소드 실행 후 락 해제
      }
    }
  }
}
