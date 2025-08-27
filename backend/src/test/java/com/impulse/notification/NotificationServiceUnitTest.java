package com.impulse.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// InjectMocks removed: we construct the service explicitly in the test
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.impulse.analytics.EventTracker;

@ExtendWith(MockitoExtension.class)
class NotificationServiceUnitTest {

    @Mock
    JdbcTemplate jdbc;

    @Mock
    EventTracker tracker;

    NotificationService notificationService;

    @Test
    void sendNotification_smoke(){
        Map<String,Object> prefs = Map.of(
            "allow_email", true,
            "allow_push", true,
            "tz", "UTC",
            // set quiet hours to an early-morning window that does not include typical test time
            "quiet_hours_start", java.sql.Time.valueOf("01:00:00"),
            "quiet_hours_end", java.sql.Time.valueOf("02:00:00"),
            "max_per_day", 10,
            "max_per_week", 100
        );
    // return prefs when fetching user prefs
    org.mockito.Mockito.doReturn(prefs).when(jdbc).queryForMap(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(1L));
        // counts (per day / per week) should be 0 to allow send
        when(jdbc.queryForObject(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(Integer.class), org.mockito.ArgumentMatchers.eq(1L))).thenReturn(0);

    // construct service with explicit Clock so it has a non-null clock in tests
    notificationService = new NotificationService(jdbc, tracker, java.time.Clock.systemUTC());

    boolean sent = notificationService.trySend(1L, "email", "welcome");
        assertThat(sent).isTrue();
    }
}
