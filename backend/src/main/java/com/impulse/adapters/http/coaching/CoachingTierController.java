package com.impulse.adapters.http.coaching;

import com.impulse.application.coaching.dto.CoachingTierResponse;
import com.impulse.application.coaching.port.in.GetCoachingTierQuery;
import com.impulse.domain.coaching.CoachingTier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coaching/tiers")
public class CoachingTierController {

    private final GetCoachingTierQuery getCoachingTierQuery;

    public CoachingTierController(GetCoachingTierQuery getCoachingTierQuery) {
        this.getCoachingTierQuery = getCoachingTierQuery;
    }

    @GetMapping
    public ResponseEntity<List<CoachingTierResponse>> getAllTiers() {
        List<CoachingTier> tiers = getCoachingTierQuery.findAll();
        List<CoachingTierResponse> response = tiers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tier}")
    public ResponseEntity<CoachingTierResponse> getTierByName(@PathVariable String tier) {
        return getCoachingTierQuery.findByTier(tier)
                .map(t -> ResponseEntity.ok(toResponse(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<CoachingTierResponse>> getTiersByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<CoachingTier> tiers = getCoachingTierQuery.findByPriceRange(minPrice, maxPrice);
        List<CoachingTierResponse> response = tiers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CoachingTierResponse toResponse(CoachingTier tier) {
        return new CoachingTierResponse(
                tier.getTier(),
                tier.getName(),
                tier.getDescription(),
                tier.getMonthlyPrice(),
                tier.getResponseTimeHours(),
                tier.getMonthlyInteractions(),
                tier.includesVideoCalls(),
                tier.hasPersonalizedPlan(),
                tier.hasPrioritySupport()
        );
    }
}
