package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Minimal email service storing templates and recording send events.
 * Stub (no external provider) centralizing logic for future extension.
 */
@Service
public class EmailService {
    private final JdbcTemplate jdbc;
    public EmailService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public Optional<Map<String,Object>> templateByCode(String code){
        try {
            return Optional.ofNullable(jdbc.queryForMap("SELECT subject, body FROM email_templates WHERE code=?", code));
        } catch(Exception e){ return Optional.empty(); }
    }

    @Transactional
    public Map<String,Object> send(String code, Long userId, Map<String,Object> params){
        var tmpl = templateByCode(code).orElseThrow(() -> new IllegalArgumentException("email_template_not_found"));
        String rendered = render((String)tmpl.get("body"), params);
        jdbc.update("INSERT INTO email_sends(template_code,user_id,rendered_body,sent_at) VALUES (?,?,?,CURRENT_TIMESTAMP)", code, userId, rendered);
        return Map.of(
            "template", code,
            "user_id", userId,
            "sent_at", Instant.now().toString(),
            "preview", rendered.substring(0, Math.min(rendered.length(), 140))
        );
    }

    private String render(String body, Map<String,Object> params){
        String out = body;
        if(params!=null){
            for(var e: params.entrySet()){
                out = out.replace("{{"+e.getKey()+"}}", String.valueOf(e.getValue()));
            }
        }
        return out;
    }
}
