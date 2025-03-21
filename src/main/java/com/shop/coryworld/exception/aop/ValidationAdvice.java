package com.shop.coryworld.exception.aop;

import com.shop.coryworld.exception.NoAuthorizationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ValidationAdvice {

    /**
     * 맨처음 있는 에스터리스크(*)는 public, protected, static 같은 접근제한자를 지정하는 영역이다.
     * 두번째로 com.shop.coryworld.controller 은 적용될 패키지 경로를 지정하는 영역이다.
     * 세번째로 *Controller는 파일명이 Controller로 끝나는 파일을 지정하는 영역이다.
     * 네번째로 *(..)는 해당 경로에 있는 메서드를 지정하는 영역이다.
     *
     * proceedingJoinPoint는 지정된 경로에 있는 함수가 실행될 때,
     * 그 함수의 모든 정보에 접근할 수 있도록 해주는 인터페이스이다.
     * proceed()는 해당 함수로 돌아가도록 해주는 함수이다.
     *
     * ApiController나 Controller가 가진 메서드들이 실행될 때,
     * 그 메서드가 가지고 있는 모든 정보를 proceedingJoinPoint에 담아오고,
     * @Around 로 인해 ApiController나 Controller가 가진 메서드가 실행되기 전에
     * 지금 만든 advice 메서드들이 먼저 실행이된 후,
     * 다시 Controller가 가진 메서드로 복귀하여 그 메서드가 실행되도록 리턴
     */

    @Around("execution(* com.shop.coryworld.controller.ItemController.*(..))")
    public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof User user) {
                if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    throw new NoAuthorizationException("접근 권한이 없습니다.");
                }
            }
        }

        return joinPoint.proceed();
    }
}
