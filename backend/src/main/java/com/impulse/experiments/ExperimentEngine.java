package com.impulse.experiments;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;import java.security.MessageDigest;import java.security.NoSuchAlgorithmException;import java.util.*;import java.nio.ByteBuffer;

@Service
public class ExperimentEngine {
    private final Map<String,List<String>> experiments = new HashMap<>();
    public ExperimentEngine(){
        // define simple experiments (feature -> variants)
        experiments.put("onboarding_flow", List.of("control","fast_track"));
        experiments.put("paywall_copy", List.of("A","B","C"));
    }
    public String variant(String experiment, long userId){
        var variants = experiments.get(experiment); if(variants==null || variants.isEmpty()) return "control";
        int bucket = Math.abs(hash(experiment+":"+userId)) % variants.size();
        return variants.get(bucket);
    }
    private int hash(String key){
        try { MessageDigest md = MessageDigest.getInstance("MD5"); return ByteBuffer.wrap(md.digest(key.getBytes(StandardCharsets.UTF_8))).getInt(); } catch (NoSuchAlgorithmException e){ return key.hashCode(); }
    }
}
