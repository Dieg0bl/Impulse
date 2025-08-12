package com.impulse.infrastructure.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class Procedures {
    private final JdbcTemplate jdbc;

    public void callGdprErasure(Long userId, String actor) {
        jdbc.execute((conn) -> {
            CallableStatement cs = conn.prepareCall("{CALL sp_user_gdpr_erasure(?, ?)}");
            cs.setObject(1, userId, Types.BIGINT);
            cs.setString(2, actor);
            return cs;
        });
    }

    // Ejemplo de otro SP ya existente
    public Map<String,Object> insertAuditoriaAvanzada(Long userId, String accion, String detalles,
          String ip, String ua, String modulo, String resultado, String nivelCriticidad) {
        return jdbc.call(conn -> {
            CallableStatement cs = conn.prepareCall("{CALL sp_insert_auditoria_avanzada(?,?,?,?,?,?,?, ?,?)}");
            cs.setObject(1, userId); cs.setString(2, accion); cs.setString(3, detalles);
            cs.setString(4, ip); cs.setString(5, ua);
            cs.setString(6, "{}"); // metadatos JSON si aplica
            cs.setString(7, nivelCriticidad); cs.setString(8, modulo); cs.setString(9, resultado);
            return cs;
        }, List.of());
    }

    // Puedes añadir aquí otros SPs según necesidad
}
