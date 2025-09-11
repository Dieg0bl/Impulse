package com.impulse.lean.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemMonitoringService {

    private final Map<String, Object> systemMetrics = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> performanceHistory = new ArrayList<>();

    @Scheduled(fixedRate = 60000) // Every minute
    public void collectSystemMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // System health metrics
            metrics.put("timestamp", LocalDateTime.now());
            metrics.put("cpuUsage", getCpuUsage());
            metrics.put("memoryUsage", getMemoryUsage());
            metrics.put("diskUsage", getDiskUsage());
            metrics.put("networkActivity", getNetworkActivity());
            
            // Application metrics
            metrics.put("activeUsers", getActiveUsersCount());
            metrics.put("requestsPerMinute", getRequestsPerMinute());
            metrics.put("averageResponseTime", getAverageResponseTime());
            metrics.put("errorRate", getErrorRate());
            
            // Database metrics
            metrics.put("dbConnections", getDatabaseConnections());
            metrics.put("dbQueryTime", getDatabaseQueryTime());
            metrics.put("dbLockWaits", getDatabaseLockWaits());
            
            systemMetrics.putAll(metrics);
            
            // Keep last 1440 measurements (24 hours at 1-minute intervals)
            if (performanceHistory.size() >= 1440) {
                performanceHistory.remove(0);
            }
            performanceHistory.add(new HashMap<>(metrics));
            
        } catch (Exception e) {
            System.err.println("Error collecting system metrics: " + e.getMessage());
        }
    }

    public Map<String, Object> getCurrentMetrics() {
        Map<String, Object> current = new HashMap<>(systemMetrics);
        current.put("lastUpdated", LocalDateTime.now());
        return current;
    }

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Overall system status
            String status = determineSystemStatus();
            health.put("status", status);
            health.put("uptime", getSystemUptime());
            health.put("version", "1.0.0");
            
            // Service health checks
            Map<String, String> services = new HashMap<>();
            services.put("database", checkDatabaseHealth());
            services.put("redis", checkRedisHealth());
            services.put("stripe", checkStripeHealth());
            services.put("email", checkEmailServiceHealth());
            services.put("storage", checkStorageHealth());
            health.put("services", services);
            
            // Resource utilization
            Map<String, Double> resources = new HashMap<>();
            resources.put("cpu", (Double) systemMetrics.getOrDefault("cpuUsage", 0.0));
            resources.put("memory", (Double) systemMetrics.getOrDefault("memoryUsage", 0.0));
            resources.put("disk", (Double) systemMetrics.getOrDefault("diskUsage", 0.0));
            health.put("resources", resources);
            
            // Performance indicators
            Map<String, Object> performance = new HashMap<>();
            performance.put("responseTime", systemMetrics.getOrDefault("averageResponseTime", 0.0));
            performance.put("throughput", systemMetrics.getOrDefault("requestsPerMinute", 0));
            performance.put("errorRate", systemMetrics.getOrDefault("errorRate", 0.0));
            health.put("performance", performance);
            
        } catch (Exception e) {
            health.put("status", "ERROR");
            health.put("error", e.getMessage());
        }
        
        return health;
    }

    public List<Map<String, Object>> getPerformanceHistory(int hours) {
        int records = Math.min(hours * 60, performanceHistory.size());
        return performanceHistory.subList(
            Math.max(0, performanceHistory.size() - records), 
            performanceHistory.size()
        );
    }

    public Map<String, Object> getDetailedSystemMetrics() {
        Map<String, Object> detailed = new HashMap<>();
        
        // Current metrics
        detailed.putAll(systemMetrics);
        
        // Historical trends
        Map<String, Object> trends = calculateTrends();
        detailed.put("trends", trends);
        
        // Alerts and warnings
        List<Map<String, Object>> alerts = generateAlerts();
        detailed.put("alerts", alerts);
        
        // Capacity planning
        Map<String, Object> capacity = calculateCapacityMetrics();
        detailed.put("capacity", capacity);
        
        return detailed;
    }

    // Health check methods
    private String checkDatabaseHealth() {
        try {
            // Mock database health check
            double responseTime = (Double) systemMetrics.getOrDefault("dbQueryTime", 0.0);
            return responseTime < 100 ? "UP" : "SLOW";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String checkRedisHealth() {
        try {
            // Mock Redis health check
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String checkStripeHealth() {
        try {
            // Mock Stripe API health check
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String checkEmailServiceHealth() {
        try {
            // Mock email service health check
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String checkStorageHealth() {
        try {
            // Mock storage health check
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    // Metric collection methods
    private double getCpuUsage() {
        // Mock CPU usage - would integrate with system monitoring
        return 35.0 + Math.random() * 30;
    }

    private double getMemoryUsage() {
        // Mock memory usage
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long total = runtime.maxMemory();
        return (double) used / total * 100;
    }

    private double getDiskUsage() {
        // Mock disk usage
        return 25.0 + Math.random() * 20;
    }

    private Map<String, Double> getNetworkActivity() {
        Map<String, Double> network = new HashMap<>();
        network.put("inbound", 125.4 + Math.random() * 50);
        network.put("outbound", 89.7 + Math.random() * 40);
        return network;
    }

    private int getActiveUsersCount() {
        // Mock active users count
        return 1000 + (int)(Math.random() * 500);
    }

    private int getRequestsPerMinute() {
        // Mock requests per minute
        return 200 + (int)(Math.random() * 100);
    }

    private double getAverageResponseTime() {
        // Mock average response time
        return 100.0 + Math.random() * 50;
    }

    private double getErrorRate() {
        // Mock error rate
        return Math.random() * 2;
    }

    private int getDatabaseConnections() {
        // Mock database connections
        return 15 + (int)(Math.random() * 10);
    }

    private double getDatabaseQueryTime() {
        // Mock database query time
        return 25.0 + Math.random() * 15;
    }

    private int getDatabaseLockWaits() {
        // Mock database lock waits
        return (int)(Math.random() * 5);
    }

    private String getSystemUptime() {
        // Mock system uptime
        return "15d 7h 32m";
    }

    private String determineSystemStatus() {
        double cpu = (Double) systemMetrics.getOrDefault("cpuUsage", 0.0);
        double memory = (Double) systemMetrics.getOrDefault("memoryUsage", 0.0);
        double errorRate = (Double) systemMetrics.getOrDefault("errorRate", 0.0);
        
        if (cpu > 90 || memory > 90 || errorRate > 5) {
            return "CRITICAL";
        } else if (cpu > 70 || memory > 70 || errorRate > 2) {
            return "WARNING";
        } else {
            return "HEALTHY";
        }
    }

    private Map<String, Object> calculateTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        if (performanceHistory.size() < 60) {
            trends.put("status", "INSUFFICIENT_DATA");
            return trends;
        }
        
        // Calculate trends for the last hour vs previous hour
        List<Map<String, Object>> lastHour = performanceHistory.subList(
            performanceHistory.size() - 60, performanceHistory.size()
        );
        List<Map<String, Object>> previousHour = performanceHistory.subList(
            performanceHistory.size() - 120, performanceHistory.size() - 60
        );
        
        // CPU trend
        double avgCpuLast = lastHour.stream()
            .mapToDouble(m -> (Double) m.getOrDefault("cpuUsage", 0.0))
            .average().orElse(0.0);
        double avgCpuPrev = previousHour.stream()
            .mapToDouble(m -> (Double) m.getOrDefault("cpuUsage", 0.0))
            .average().orElse(0.0);
        trends.put("cpuTrend", calculatePercentageChange(avgCpuPrev, avgCpuLast));
        
        // Memory trend
        double avgMemLast = lastHour.stream()
            .mapToDouble(m -> (Double) m.getOrDefault("memoryUsage", 0.0))
            .average().orElse(0.0);
        double avgMemPrev = previousHour.stream()
            .mapToDouble(m -> (Double) m.getOrDefault("memoryUsage", 0.0))
            .average().orElse(0.0);
        trends.put("memoryTrend", calculatePercentageChange(avgMemPrev, avgMemLast));
        
        // Response time trend
        double avgRespLast = lastHour.stream()
            .mapToDouble(m -> (Double) m.getOrDefault("averageResponseTime", 0.0))
            .average().orElse(0.0);
        double avgRespPrev = previousHour.stream()
            .mapToDouble(m -> (Double) m.getOrDefault("averageResponseTime", 0.0))
            .average().orElse(0.0);
        trends.put("responseTimeTrend", calculatePercentageChange(avgRespPrev, avgRespLast));
        
        return trends;
    }

    private List<Map<String, Object>> generateAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        double cpu = (Double) systemMetrics.getOrDefault("cpuUsage", 0.0);
        double memory = (Double) systemMetrics.getOrDefault("memoryUsage", 0.0);
        double errorRate = (Double) systemMetrics.getOrDefault("errorRate", 0.0);
        double responseTime = (Double) systemMetrics.getOrDefault("averageResponseTime", 0.0);
        
        if (cpu > 80) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", cpu > 90 ? "CRITICAL" : "WARNING");
            alert.put("metric", "CPU Usage");
            alert.put("value", cpu);
            alert.put("threshold", cpu > 90 ? 90 : 80);
            alert.put("message", "High CPU usage detected");
            alert.put("timestamp", LocalDateTime.now());
            alerts.add(alert);
        }
        
        if (memory > 80) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", memory > 90 ? "CRITICAL" : "WARNING");
            alert.put("metric", "Memory Usage");
            alert.put("value", memory);
            alert.put("threshold", memory > 90 ? 90 : 80);
            alert.put("message", "High memory usage detected");
            alert.put("timestamp", LocalDateTime.now());
            alerts.add(alert);
        }
        
        if (errorRate > 1) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", errorRate > 5 ? "CRITICAL" : "WARNING");
            alert.put("metric", "Error Rate");
            alert.put("value", errorRate);
            alert.put("threshold", errorRate > 5 ? 5 : 1);
            alert.put("message", "High error rate detected");
            alert.put("timestamp", LocalDateTime.now());
            alerts.add(alert);
        }
        
        if (responseTime > 200) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", responseTime > 500 ? "CRITICAL" : "WARNING");
            alert.put("metric", "Response Time");
            alert.put("value", responseTime);
            alert.put("threshold", responseTime > 500 ? 500 : 200);
            alert.put("message", "High response time detected");
            alert.put("timestamp", LocalDateTime.now());
            alerts.add(alert);
        }
        
        return alerts;
    }

    private Map<String, Object> calculateCapacityMetrics() {
        Map<String, Object> capacity = new HashMap<>();
        
        // Current utilization
        double cpu = (Double) systemMetrics.getOrDefault("cpuUsage", 0.0);
        double memory = (Double) systemMetrics.getOrDefault("memoryUsage", 0.0);
        int activeUsers = (Integer) systemMetrics.getOrDefault("activeUsers", 0);
        
        // Capacity thresholds
        capacity.put("cpuCapacity", Math.max(0, 100 - cpu));
        capacity.put("memoryCapacity", Math.max(0, 100 - memory));
        capacity.put("userCapacity", Math.max(0, 5000 - activeUsers)); // Assuming 5000 user limit
        
        // Time to capacity (rough estimates)
        if (cpu > 50) {
            capacity.put("cpuTimeToCapacity", "2-3 weeks");
        } else {
            capacity.put("cpuTimeToCapacity", "6+ months");
        }
        
        if (memory > 60) {
            capacity.put("memoryTimeToCapacity", "4-6 weeks");
        } else {
            capacity.put("memoryTimeToCapacity", "6+ months");
        }
        
        return capacity;
    }

    private double calculatePercentageChange(double previous, double current) {
        if (previous == 0) return 0;
        return ((current - previous) / previous) * 100;
    }
}
