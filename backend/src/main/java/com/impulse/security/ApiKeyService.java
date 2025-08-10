package com.impulse.security;

import com.impulse.domain.security.ApiKey;
import com.impulse.infrastructure.security.ApiKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
public class ApiKeyService {
    private final ApiKeyRepository repo;
    private final SecurityAuditService audit;
    private final SecureRandom random = new SecureRandom();

    public ApiKeyService(ApiKeyRepository repo, SecurityAuditService audit){this.repo=repo; this.audit=audit;}

    @Transactional
    public Map<String,String> createKey(String name, Long actorUserId, String ip){
        String raw = generateRawKey();
        String hash = sha256(raw);
        ApiKey entity = new ApiKey(name, hash);
        repo.save(entity);
        audit.audit(actorUserId, ip, "API_KEY_CREATED", "api_key", String.valueOf(entity.getId()), Map.of("name", name), "high");
        return Map.of("id", String.valueOf(entity.getId()), "name", name, "apiKey", raw);
    }

    public List<ApiKey> listActive(){ return repo.findAll().stream().filter(k-> !k.isRevoked()).toList(); }

    @Transactional
    public boolean revoke(Long id, Long actorUserId, String ip){
        return repo.findById(id).map(k->{
            if(!k.isRevoked()){ k.revoke(); audit.audit(actorUserId, ip, "API_KEY_REVOKED", "api_key", String.valueOf(k.getId()), Map.of("name", k.getName()), "high"); }
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean validateAndTouch(String rawKey, String ip){
        String hash = sha256(rawKey);
        return repo.findByKeyHash(hash).map(k->{
            if(k.isRevoked()) return false;
            k.markUsed();
            return true;
        }).orElse(false);
    }

    private String generateRawKey(){
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
    private String sha256(String raw){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes()));
        }catch(Exception e){ throw new IllegalStateException("hash_error", e); }
    }
}
