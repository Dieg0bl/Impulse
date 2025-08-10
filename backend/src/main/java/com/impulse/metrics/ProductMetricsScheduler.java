package com.impulse.metrics;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDate;

@Component
public class ProductMetricsScheduler {
    private final ProductMetricsService service; private final JdbcTemplate jdbc;
    public ProductMetricsScheduler(ProductMetricsService service, JdbcTemplate jdbc){ this.service=service; this.jdbc=jdbc; }

    // Ejecutar cada noche a las 02:10
    @Scheduled(cron = "0 10 2 * * *")
    public void nightly(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        bootstrapCohorts(yesterday);
        service.aggregateDaily(yesterday);
    }

    private void bootstrapCohorts(LocalDate day){
        // Insertar usuarios nuevos del día en cohort_users si no existen
        jdbc.update("INSERT IGNORE INTO cohort_users(user_id,cohort_date,first_activity_date) SELECT id, ?, NULL FROM usuarios WHERE DATE(created_at)=?", day, day);
        // Actualizar first_activity_date si hay eventos (primer evento)
        jdbc.update("UPDATE cohort_users c JOIN (SELECT user_id, MIN(DATE(timestamp)) d FROM events GROUP BY user_id) e ON c.user_id=e.user_id SET c.first_activity_date=e.d WHERE c.first_activity_date IS NULL");
    }
}
