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

    @Around("@annotation(com.impulse.monetizacion.RequiresPlan) || within(@com.impulse.monetizacion.RequiresPlan *)")
    public Object checkEntitlement(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String feature = "secured";
        RequiresPlan rp = ms.getMethod().getAnnotation(RequiresPlan.class);
        if(rp==null && pjp.getTarget()!=null){
            rp = pjp.getTarget().getClass().getAnnotation(RequiresPlan.class);
        }
        if(rp!=null) feature = rp.value();
        Long userId = resolveUserId(pjp.getArgs());
        if(userId==null){
            // fallback a SecurityContext si existe
            try {
                Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if(principal instanceof com.impulse.domain.usuario.Usuario u){ userId = u.getId(); }
            } catch (Exception ignored) {}
        }
        if(userId==null) throw new IllegalStateException("userId_no_disponible_para_entitlement");
        if(!paywall.hasEntitlement(userId, feature)){
            throw new IllegalStateException("Entitlement requerido:" + feature);
        }
        return pjp.proceed();
    }

    private Long resolveUserId(Object[] args){
        if(args==null) return null;
        for(Object o: args){
            if(o==null) continue;
            if(o instanceof Long l) return l;
            try { // DTOs con getUsuarioId
                var m = o.getClass().getMethod("getUsuarioId");
                Object v = m.invoke(o);
                if(v instanceof Long l2) return l2;
            } catch (ReflectiveOperationException ignored) { }
        }
        return null;
    }
}
