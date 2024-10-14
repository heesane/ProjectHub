package com.project.hub.aop.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DistributedLock {

  long waitTime() default 5L;   // 락을 기다릴 최대 시간 (초)

  long leaseTime() default 3L;   // 락을 유지할 시간 (초)
}
