package com.impulse.application;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.domain.usuario.ReferralCode;
import com.impulse.domain.usuario.ReferralUse;
import com.impulse.analytics.EventTracker;
import com.impulse.infrastructure.usuario.ReferralCodeRepository;
import com.impulse.infrastructure.usuario.ReferralUseRepository;

@Service
public class ReferralService {
    private final ReferralCodeRepository codeRepo;
    private final ReferralUseRepository useRepo;
    private final SecureRandom random = new SecureRandom();
    private final EventTracker tracker;

    public ReferralService(ReferralCodeRepository codeRepo, ReferralUseRepository useRepo, EventTracker tracker){
        this.codeRepo = codeRepo;
        this.useRepo = useRepo;
        this.tracker = tracker;
    }

    @Transactional
    public ReferralCode generate(Long userId){
        if(codeRepo.countByUserId(userId) >= 5) throw new IllegalStateException("max codes reached");
        String code;
        do { code = HexFormat.of().formatHex(random.generateSeed(5)); } while(codeRepo.findByCode(code).isPresent());
        return codeRepo.save(new ReferralCode(userId, code));
    }

    @Transactional
    public ReferralUse apply(String code, Long referredUserId){
        var rc = codeRepo.findByCode(code).orElseThrow();
        if(useRepo.countByReferredUserId(referredUserId) > 0) throw new IllegalStateException("already referred");
        var use = useRepo.save(new ReferralUse(rc.getId(), referredUserId));
        // Emit event for referral usage
        tracker.track(referredUserId, "referral_used", Map.of(
            "code_id", rc.getId(),
            "referrer_user_id", rc.getUserId()
        ), null, "backend");
        // Reward release once: first successful use triggers reward for referrer
        if(!rc.isRewardReleased()){
            rc.markRewardReleased();
            codeRepo.save(rc);
            tracker.track(rc.getUserId(), "referral_reward_released", Map.of(
                "code_id", rc.getId(),
                "referred_user_id", referredUserId
            ), null, "backend");
        }
        return use;
    }

    public Map<String,Object> stats(Long userId){
        Map<String,Object> out = new HashMap<>();
        long codes = codeRepo.countByUserId(userId);
        out.put("codes", codes);
        return out;
    }
}
