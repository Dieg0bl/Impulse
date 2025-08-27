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
    public static class AuditoriaAvanzadaParams {
        private Long userId;
        private String accion;
        private String detalles;
        private String ip;
        private String ua;
        private String modulo;
        private String resultado;
        private String nivelCriticidad;

    public AuditoriaAvanzadaParams(Long userId, String accion, String detalles, String ip, String ua, String modulo, String resultado, String nivelCriticidad) {
            this.userId = userId;
            this.accion = accion;
            this.detalles = detalles;
            this.ip = ip;
            this.ua = ua;
            this.modulo = modulo;
            this.resultado = resultado;
            this.nivelCriticidad = nivelCriticidad;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }
        public String getDetalles() { return detalles; }
        public void setDetalles(String detalles) { this.detalles = detalles; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getUa() { return ua; }
        public void setUa(String ua) { this.ua = ua; }
        public String getModulo() { return modulo; }
        public void setModulo(String modulo) { this.modulo = modulo; }
        public String getResultado() { return resultado; }
        public void setResultado(String resultado) { this.resultado = resultado; }
        public String getNivelCriticidad() { return nivelCriticidad; }
        public void setNivelCriticidad(String nivelCriticidad) { this.nivelCriticidad = nivelCriticidad; }
    }

    public Map<String,Object> insertAuditoriaAvanzada(AuditoriaAvanzadaParams params) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<java.util.Map<String,Object>>) conn -> {
            try (CallableStatement cs = conn.prepareCall("{CALL sp_insert_auditoria_avanzada(?,?,?,?,?,?,?, ?,?)}")) {
                cs.setObject(1, params.userId); cs.setString(2, params.accion); cs.setString(3, params.detalles);
                cs.setString(4, params.ip); cs.setString(5, params.ua);
                cs.setString(6, "{}"); // metadatos JSON si aplica
                cs.setString(7, params.nivelCriticidad); cs.setString(8, params.modulo); cs.setString(9, params.resultado);
                cs.execute();
            }
            return java.util.Map.of();
        });
    }

    // Puedes añadir aquí otros SPs según necesidad
}
