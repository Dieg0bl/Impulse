package com.impulse.infrastructure.db;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Procedures {
    private final JdbcTemplate jdbc;

    public Procedures(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void callGdprErasure(Long userId, String actor) {
        jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Void>) conn -> {
            try (CallableStatement cs = conn.prepareCall("{CALL sp_user_gdpr_erasure(?, ?)}")) {
                cs.setObject(1, userId, Types.BIGINT);
                cs.setString(2, actor);
                cs.execute();
            }
            return null;
        });
    }

    // Ejemplo de otro SP ya existente
    public Map<String,Object> insertAuditoriaAvanzada(Long userId, String accion, String detalles,
          String ip, String ua, String modulo, String resultado, String nivelCriticidad) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<java.util.Map<String,Object>>) conn -> {
            try (CallableStatement cs = conn.prepareCall("{CALL sp_insert_auditoria_avanzada(?,?,?,?,?,?,?, ?,?)}")) {
                cs.setObject(1, userId); cs.setString(2, accion); cs.setString(3, detalles);
                cs.setString(4, ip); cs.setString(5, ua);
                cs.setString(6, "{}"); // metadatos JSON si aplica
                cs.setString(7, nivelCriticidad); cs.setString(8, modulo); cs.setString(9, resultado);
                cs.execute();
            }
            return java.util.Map.of();
        });
    }

    // Puedes añadir aquí otros SPs según necesidad
}
