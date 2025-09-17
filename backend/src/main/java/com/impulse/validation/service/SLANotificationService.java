package com.impulse.validation.service;

import com.impulse.validation.SLANotification;
import com.impulse.validation.repository.SLANotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SLANotificationService {
    private final SLANotificationRepository slaNotificationRepository;
    public SLANotificationService(SLANotificationRepository slaNotificationRepository) {
        this.slaNotificationRepository = slaNotificationRepository;
    }
    public List<SLANotification> getAllSLANotifications() {
        return slaNotificationRepository.findAll();
    }
}
