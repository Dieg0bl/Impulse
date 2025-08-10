package com.impulse.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import com.impulse.infrastructure.usuario.ReferralCodeRepository;
import com.impulse.infrastructure.usuario.ReferralUseRepository;

@Service
public class ReferralFraudService {
    private final ReferralCodeRepository codeRepo;
    private final JdbcTemplate jdbc;

    public ReferralFraudService(ReferralCodeRepository codeRepo, ReferralUseRepository useRepo, JdbcTemplate jdbc){
        this.codeRepo = codeRepo;
        this.jdbc = jdbc; // useRepo intentionally not stored (unused)
    }

    @Transactional(readOnly=true)
    public boolean suspiciousUser(Long userId){
        long codes = codeRepo.countByUserId(userId);
        boolean suspicious = codes > 5; // heurística inicial
        if(suspicious){
            jdbc.update("INSERT INTO referral_fraud_audit(user_id, signal, details) VALUES (?,?,?)", userId, "CODE_ABUSE", "codes="+codes);
        }
        return suspicious;
    }
}
