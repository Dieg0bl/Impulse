package com.impulse.freemium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import com.impulse.freemium.dto.*;

@RestController
@RequestMapping("/api/freemium")
public class FreemiumController {
    @Autowired
    private FreemiumService freemiumService;

    @GetMapping("/tiers")
    public List<SubscriptionTierDTO> getTiers() {
        return freemiumService.getAllTiers().stream().map(FreemiumMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/tiers/{id}")
    public SubscriptionTierDTO getTier(@PathVariable String id) {
        SubscriptionTier tier = freemiumService.getTierById(id);
        return tier == null ? null : FreemiumMapper.toDTO(tier);
    }

    @GetMapping("/upgrade-paths")
    public List<UpgradePathDTO> getPaths() {
        return freemiumService.getUpgradePaths().stream().map(FreemiumMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/upgrade-experiences")
    public List<UpgradeExperienceDTO> getExperiences() {
        return freemiumService.getUpgradeExperiences().stream().map(FreemiumMapper::toDTO).collect(Collectors.toList());
    }

    // Endpoint para upgrade de tier
    @PostMapping("/upgrade")
    public boolean upgradeTier(@RequestParam String userId, @RequestParam String targetTier) {
        return freemiumService.upgradeTier(userId, targetTier);
    }

    // Endpoint para activar trial
    @PostMapping("/trial")
    public boolean startTrial(@RequestParam String userId, @RequestParam String targetTier) {
        return freemiumService.startTrial(userId, targetTier);
    }

    @GetMapping("/subscription-status")
    public SubscriptionStatusDTO getStatus(@RequestParam String userId){
        return freemiumService.getSubscriptionStatus(userId);
    }
}
