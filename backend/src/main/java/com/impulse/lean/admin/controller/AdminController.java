package com.impulse.lean.admin.controller;

import com.impulse.lean.admin.service.AdminService;
import com.impulse.lean.admin.service.AdminDashboardService;
import com.impulse.lean.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AdminDashboardService dashboardService;

    // Dashboard Metrics
    @GetMapping("/dashboard/metrics")
    @PreAuthorize("hasAuthority('ADMIN_DASHBOARD_READ')")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        try {
            Map<String, Object> metrics = dashboardService.getDashboardMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error getting dashboard metrics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dashboard/real-time-stats")
    @PreAuthorize("hasAuthority('ADMIN_DASHBOARD_READ')")
    public ResponseEntity<Map<String, Object>> getRealTimeStats() {
        try {
            Map<String, Object> stats = dashboardService.getRealTimeStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting real-time stats: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dashboard/revenue-analytics")
    @PreAuthorize("hasAuthority('ADMIN_REVENUE_READ')")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> analytics = dashboardService.getRevenueAnalytics(days);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error getting revenue analytics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // User Management
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN_USER_READ')")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Map<String, Object> users = adminService.getAllUsers(pageable, search, status);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error getting users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_USER_READ')")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable Long userId) {
        try {
            Map<String, Object> userDetails = adminService.getUserDetails(userId);
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            log.error("Error getting user details for ID {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/users/{userId}/suspend")
    @PreAuthorize("hasAuthority('ADMIN_USER_SUSPEND')")
    public ResponseEntity<ApiResponse<String>> suspendUser(
            @PathVariable Long userId,
            @RequestBody @Valid SuspendUserRequest request) {
        try {
            adminService.suspendUser(userId, request.getReason(), request.getDuration());
            return ResponseEntity.ok(new ApiResponse<>(true, "User suspended successfully", null));
        } catch (Exception e) {
            log.error("Error suspending user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error suspending user", null));
        }
    }

    @PostMapping("/users/{userId}/activate")
    @PreAuthorize("hasAuthority('ADMIN_USER_ACTIVATE')")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long userId) {
        try {
            adminService.activateUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "User activated successfully", null));
        } catch (Exception e) {
            log.error("Error activating user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error activating user", null));
        }
    }

    @PostMapping("/users/{userId}/ban")
    @PreAuthorize("hasAuthority('ADMIN_USER_BAN')")
    public ResponseEntity<ApiResponse<String>> banUser(
            @PathVariable Long userId,
            @RequestBody @Valid BanUserRequest request) {
        try {
            adminService.banUser(userId, request.getReason(), request.isPermanent());
            return ResponseEntity.ok(new ApiResponse<>(true, "User banned successfully", null));
        } catch (Exception e) {
            log.error("Error banning user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error banning user", null));
        }
    }

    // System Health & Monitoring
    @GetMapping("/system/health")
    @PreAuthorize("hasAuthority('ADMIN_SYSTEM_READ')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> health = adminService.getSystemHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error getting system health: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/system/metrics")
    @PreAuthorize("hasAuthority('ADMIN_SYSTEM_READ')")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        try {
            Map<String, Object> metrics = adminService.getSystemMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error getting system metrics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Content Moderation
    @GetMapping("/moderation/queue")
    @PreAuthorize("hasAuthority('ADMIN_MODERATION_READ')")
    public ResponseEntity<Map<String, Object>> getModerationQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Map<String, Object> queue = adminService.getModerationQueue(pageable);
            return ResponseEntity.ok(queue);
        } catch (Exception e) {
            log.error("Error getting moderation queue: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/moderation/{contentId}/approve")
    @PreAuthorize("hasAuthority('ADMIN_MODERATION_WRITE')")
    public ResponseEntity<ApiResponse<String>> approveContent(@PathVariable Long contentId) {
        try {
            adminService.approveContent(contentId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Content approved successfully", null));
        } catch (Exception e) {
            log.error("Error approving content {}: {}", contentId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error approving content", null));
        }
    }

    @PostMapping("/moderation/{contentId}/reject")
    @PreAuthorize("hasAuthority('ADMIN_MODERATION_WRITE')")
    public ResponseEntity<ApiResponse<String>> rejectContent(
            @PathVariable Long contentId,
            @RequestBody @Valid RejectContentRequest request) {
        try {
            adminService.rejectContent(contentId, request.getReason());
            return ResponseEntity.ok(new ApiResponse<>(true, "Content rejected successfully", null));
        } catch (Exception e) {
            log.error("Error rejecting content {}: {}", contentId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error rejecting content", null));
        }
    }

    // Reports & Analytics
    @GetMapping("/analytics/overview")
    @PreAuthorize("hasAuthority('ADMIN_ANALYTICS_READ')")
    public ResponseEntity<Map<String, Object>> getAnalyticsOverview(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> analytics = adminService.getAnalyticsOverview(days);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error getting analytics overview: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/users")
    @PreAuthorize("hasAuthority('ADMIN_REPORTS_READ')")
    public ResponseEntity<Map<String, Object>> getUsersReport(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) String format) {
        try {
            Map<String, Object> report = adminService.getUsersReport(days, format);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating users report: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/revenue")
    @PreAuthorize("hasAuthority('ADMIN_REPORTS_READ')")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) String format) {
        try {
            Map<String, Object> report = adminService.getRevenueReport(days, format);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Error generating revenue report: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Logs & Audit
    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('ADMIN_LOGS_READ')")
    public ResponseEntity<Map<String, Object>> getSystemLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String search) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Map<String, Object> logs = adminService.getSystemLogs(pageable, level, search);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error getting system logs: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/audit")
    @PreAuthorize("hasAuthority('ADMIN_AUDIT_READ')")
    public ResponseEntity<Map<String, Object>> getAuditLog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long userId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Map<String, Object> audit = adminService.getAuditLog(pageable, action, userId);
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            log.error("Error getting audit log: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Backup & Recovery
    @PostMapping("/backup/create")
    @PreAuthorize("hasAuthority('ADMIN_BACKUP_WRITE')")
    public ResponseEntity<ApiResponse<String>> createBackup() {
        try {
            String backupId = adminService.createBackup();
            return ResponseEntity.ok(new ApiResponse<>(true, "Backup created successfully", backupId));
        } catch (Exception e) {
            log.error("Error creating backup: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error creating backup", null));
        }
    }

    @GetMapping("/backup/list")
    @PreAuthorize("hasAuthority('ADMIN_BACKUP_READ')")
    public ResponseEntity<Map<String, Object>> listBackups() {
        try {
            Map<String, Object> backups = adminService.listBackups();
            return ResponseEntity.ok(backups);
        } catch (Exception e) {
            log.error("Error listing backups: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Configuration Management
    @GetMapping("/config")
    @PreAuthorize("hasAuthority('ADMIN_CONFIG_READ')")
    public ResponseEntity<Map<String, Object>> getSystemConfiguration() {
        try {
            Map<String, Object> config = adminService.getSystemConfiguration();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("Error getting system configuration: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/config")
    @PreAuthorize("hasAuthority('ADMIN_CONFIG_WRITE')")
    public ResponseEntity<ApiResponse<String>> updateSystemConfiguration(
            @RequestBody @Valid Map<String, Object> configUpdates) {
        try {
            adminService.updateSystemConfiguration(configUpdates);
            return ResponseEntity.ok(new ApiResponse<>(true, "Configuration updated successfully", null));
        } catch (Exception e) {
            log.error("Error updating configuration: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error updating configuration", null));
        }
    }

    // Mock DTOs for compilation
    private static class SuspendUserRequest {
        private String reason;
        private Integer duration; // days
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
    }

    private static class BanUserRequest {
        private String reason;
        private boolean permanent;
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public boolean isPermanent() { return permanent; }
        public void setPermanent(boolean permanent) { this.permanent = permanent; }
    }

    private static class RejectContentRequest {
        private String reason;
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
