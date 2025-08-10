package com.impulse.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
public class EmailTemplateService {
    private final JdbcTemplate jdbc;
    public EmailTemplateService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @Transactional
    public void upsert(String code, String subject, String body){
        int updated = jdbc.update("UPDATE email_templates SET subject=?, body=? WHERE code=?", subject, body, code);
        if(updated==0){
            jdbc.update("INSERT INTO email_templates(code, subject, body) VALUES (?,?,?)", code, subject, body);
        }
    }

    public Map<String,Object> get(String code){
        return jdbc.queryForMap("SELECT code, subject, body FROM email_templates WHERE code=?", code);
    }
}
