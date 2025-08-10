package com.impulse.monetizacion;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.impulse.application.PaywallService;

/**
 * Aspecto de fences de plan: protege métodos anotados en el futuro.
 * De momento expone un pointcut placeholder sobre paquete api.secured.*.
 */
@Aspect
@Component
public class PlanFencesAspect {
    private final PaywallService paywall;

    public PlanFencesAspect(PaywallService paywall){
        this.paywall = paywall;
    }

    @Around("execution(* com.impulse.api.secured..*(..)) && args(userId,..)")
    public Object checkEntitlement(ProceedingJoinPoint pjp, Long userId) throws Throwable {
        String feature = "secured";
        try {
            MethodSignature ms = (MethodSignature) pjp.getSignature();
            RequiresPlan rp = ms.getMethod().getAnnotation(RequiresPlan.class);
            if(rp == null && pjp.getTarget()!=null){
                rp = pjp.getTarget().getClass().getAnnotation(RequiresPlan.class);
            }
            if(rp != null) feature = rp.value();
        } catch (Exception ignored) {}
        if(!paywall.hasEntitlement(userId, feature)){
            throw new IllegalStateException("Entitlement requerido:" + feature);
        }
        return pjp.proceed();
    }
}
