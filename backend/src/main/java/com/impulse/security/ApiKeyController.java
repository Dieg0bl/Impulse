package com.impulse.security;

import com.impulse.domain.security.ApiKey;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security/api-keys")
public class ApiKeyController {
    private final ApiKeyService apiKeys;
    public ApiKeyController(ApiKeyService apiKeys){ this.apiKeys = apiKeys; }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Map<String,String> body, HttpServletRequest req){
        String name = body.getOrDefault("name", "generic");
        Long actorId = currentUserId();
        return ResponseEntity.ok(apiKeys.createKey(name, actorId, req.getRemoteAddr()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApiKey> list(){ return apiKeys.listActive(); }

    @PostMapping("/{id}/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> revoke(@PathVariable Long id, HttpServletRequest req){
        boolean ok = apiKeys.revoke(id, currentUserId(), req.getRemoteAddr());
        if(ok) return ResponseEntity.ok(Map.of("revoked", true));
        return ResponseEntity.status(404).body(Map.of("revoked", false));
    }

    private Long currentUserId(){
        try{
            Authentication a = SecurityContextHolder.getContext().getAuthentication();
            if(a!=null) return Long.valueOf(a.getName());
        }catch(Exception ignored){}
        return null;
    }
}
