package com.impulse.notification;

import java.sql.Time;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import com.impulse.analytics.EventTracker;

public class NotificationServiceTest {

    JdbcTemplate jdbc = Mockito.mock(JdbcTemplate.class);
    EventTracker tracker = Mockito.mock(EventTracker.class);
    NotificationService service;

    @BeforeEach
    void setup(){
    service = new NotificationService(jdbc, tracker);
    }

    @Test
    void blocksInQuietHoursDeterministic(){
        // 22:00 local (dentro de quiet hours 20:00-09:00)
        Clock fixed = Clock.fixed(Instant.parse("2025-08-09T20:00:00Z"), ZoneOffset.UTC); // 22:00 Europe/Madrid (UTC+2)
    service = new NotificationService(jdbc, tracker, fixed);
        Map<String,Object> pref = Map.of(
                "allow_email", true,
                "allow_push", true,
                "max_per_day", 2,
                "max_per_week", 6,
                "quiet_hours_start", Time.valueOf("20:00:00"),
                "quiet_hours_end", Time.valueOf("09:00:00"),
                "tz", "Europe/Madrid"
        );
        Mockito.when(jdbc.queryForMap(Mockito.anyString(), Mockito.anyLong())).thenReturn(pref);
        Mockito.when(jdbc.queryForObject(Mockito.contains("DATE(sent_at)"), Mockito.eq(Integer.class), Mockito.anyLong())).thenReturn(0);
        Mockito.when(jdbc.queryForObject(Mockito.contains("YEARWEEK"), Mockito.eq(Integer.class), Mockito.anyLong())).thenReturn(0);
        boolean sent = service.trySend(1L, "email", "deadline_warning");
        assertThat(sent).isFalse();
    }

    @Test
    void respectsDailyLimit(){
        Map<String,Object> pref = Map.of(
                "allow_email", true,
                "allow_push", true,
                "max_per_day", 1,
                "max_per_week", 10,
                "quiet_hours_start", Time.valueOf("20:00:00"),
                "quiet_hours_end", Time.valueOf("09:00:00"),
                "tz", "Europe/Madrid"
        );
        Mockito.when(jdbc.queryForMap(Mockito.anyString(), Mockito.anyLong())).thenReturn(pref);
        Mockito.when(jdbc.queryForObject(Mockito.contains("DATE(sent_at)"), Mockito.eq(Integer.class), Mockito.anyLong())).thenReturn(1); // day count
        Mockito.when(jdbc.queryForObject(Mockito.contains("YEARWEEK"), Mockito.eq(Integer.class), Mockito.anyLong())).thenReturn(1); // week count
        boolean sent = service.trySend(1L, "email", "deadline_warning");
        assertThat(sent).isFalse();
    }

    @Test
    void respectsWeeklyLimit(){
        Map<String,Object> pref = Map.of(
                "allow_email", true,
                "allow_push", true,
                "max_per_day", 10,
                "max_per_week", 1,
                "quiet_hours_start", Time.valueOf("20:00:00"),
                "quiet_hours_end", Time.valueOf("09:00:00"),
                "tz", "Europe/Madrid"
        );
        Mockito.when(jdbc.queryForMap(Mockito.anyString(), Mockito.anyLong())).thenReturn(pref);
        Mockito.when(jdbc.queryForObject(Mockito.contains("DATE(sent_at)"), Mockito.eq(Integer.class), Mockito.anyLong())).thenReturn(0); // day count
        Mockito.when(jdbc.queryForObject(Mockito.contains("YEARWEEK"), Mockito.eq(Integer.class), Mockito.anyLong())).thenReturn(1); // week count already reached
        boolean sent = service.trySend(1L, "email", "deadline_warning");
        assertThat(sent).isFalse();
    }
}
