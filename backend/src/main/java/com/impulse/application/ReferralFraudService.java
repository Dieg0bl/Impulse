package com.impulse.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import com.impulse.application.ports.ReferralCodePort;


@Service
public class ReferralFraudService {
    private final ReferralCodePort codeRepo;
    private final JdbcTemplate jdbc;

    public ReferralFraudService(ReferralCodePort codeRepo, JdbcTemplate jdbc){
        this.codeRepo = codeRepo;
        this.jdbc = jdbc;
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
