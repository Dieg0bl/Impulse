package com.impulse.validation.controller;

import com.impulse.validation.SLANotification;
import com.impulse.validation.service.SLANotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validation/sla-notifications")
public class SLANotificationController {
    private final SLANotificationService slaNotificationService;
    public SLANotificationController(SLANotificationService slaNotificationService) {
        this.slaNotificationService = slaNotificationService;
    }
    @GetMapping
    public List<SLANotification> getAllSLANotifications() {
        return slaNotificationService.getAllSLANotifications();
    }
}
