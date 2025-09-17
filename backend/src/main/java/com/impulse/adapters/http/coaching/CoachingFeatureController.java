package com.impulse.adapters.http.coaching;

import com.impulse.application.coaching.dto.CoachingFeatureResponse;
import com.impulse.application.coaching.port.in.GetCoachingFeatureQuery;
import com.impulse.domain.coaching.CoachingFeature;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coaching/features")
public class CoachingFeatureController {

    private final GetCoachingFeatureQuery getCoachingFeatureQuery;

    public CoachingFeatureController(GetCoachingFeatureQuery getCoachingFeatureQuery) {
        this.getCoachingFeatureQuery = getCoachingFeatureQuery;
    }

    @GetMapping
    public ResponseEntity<List<CoachingFeatureResponse>> getAllFeatures() {
        List<CoachingFeature> features = getCoachingFeatureQuery.findAll();
        List<CoachingFeatureResponse> response = features.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

        @GetMapping("/{id}")
    public ResponseEntity<CoachingFeatureResponse> getFeatureById(@PathVariable String id) {
        return getCoachingFeatureQuery.findById(id)
                .map(feature -> ResponseEntity.ok(toResponse(feature)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tier/{tier}")
    public ResponseEntity<List<CoachingFeatureResponse>> getFeaturesByTier(@PathVariable String tier) {
        List<CoachingFeature> features = getCoachingFeatureQuery.findByTier(tier);
        List<CoachingFeatureResponse> response = features.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CoachingFeatureResponse>> getFeaturesByType(@PathVariable String type) {
        List<CoachingFeature> features = getCoachingFeatureQuery.findByType(type);
        List<CoachingFeatureResponse> response = features.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CoachingFeatureResponse toResponse(CoachingFeature feature) {
        return new CoachingFeatureResponse(
                feature.getId(),
                feature.getName(),
                feature.getDescription(),
                feature.getType(),
                feature.isUnlimitedUsage(),
                feature.getMonthlyQuota(),
                feature.getTier()
        );
    }
}
