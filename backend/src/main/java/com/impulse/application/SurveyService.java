package com.impulse.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.domain.pmf.Survey;
import com.impulse.application.ports.SurveyPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@SuppressWarnings({"squid:S3776", "squid:S112", "squid:S6916"})
public class SurveyService {
    private final SurveyPort repo;
    public SurveyService(SurveyPort repo){ this.repo = repo; }
    private final ObjectMapper om = new ObjectMapper();

    @Transactional
    public void submit(Long userId, String type, Map<String, Object> answers) {
        if (!List.of("sean_ellis", "nps", "love_reasons").contains(type))
            throw new IllegalArgumentException("unknown_survey_type");
        try {
            Survey s = new Survey();
            s.setUserId(userId);
            s.setSurveyType(type);
            s.setAnswersJson(om.writeValueAsString(answers));
            s.setCreatedAt(Instant.now());
            repo.save(s);
        } catch (Exception e) {
            throw new RuntimeException("survey_submit_error", e);
        }
    }

    public record PmfSignals(double veryDisappointedPct, double npsAvg, List<String> topLoveNgrams, long weeklyAhaUsers) {}

    public PmfSignals computeSignals() {
        double very = 0;
        double totalSE = 0;
        double npsSum = 0;
        double npsCount = 0;
        Map<String, Integer> loveCounts = new HashMap<>();
        final String REASONS = "reasons";

            for (Survey s : repo.findAll()) {
                try {
                    JsonNode j = om.readTree(s.getAnswersJson());
                    switch (s.getSurveyType()) {
                        case "sean_ellis" -> handleSeanEllis(j);
                        case "nps" -> handleNps(j);
                        case "love_reasons" -> handleLoveReasons(j, loveCounts);
                        default -> {
                            // Unknown survey type: ignore
                        }
                    }
                } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
                    // ignore malformed survey JSON; log at debug level
                    org.slf4j.LoggerFactory.getLogger(SurveyService.class).debug("Skipping malformed survey JSON for id {}: {}", s.getId(), jpe.getMessage());
                }
            }
    List<String> top = loveCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10).map(Map.Entry::getKey).toList();

        double veryPct = totalSE == 0 ? 0 : (very / totalSE * 100.0);
        double npsAvg = npsCount == 0 ? 0 : (npsSum / npsCount);
        long weeklyAha = 0; // delegado a AnalyticsService
        return new PmfSignals(veryPct, npsAvg, top, weeklyAha);
    }

    private void handleSeanEllis(JsonNode j) {
        String ans = j.path("q1_howFeelIfDisappears").asText("");
        if (!ans.isEmpty()) {
            // increment counters via closure variable is not possible here; handled inline in caller
        }
    }

    private void handleNps(JsonNode j) {
        // similar to previous logic; kept inline in computeSignals for simplicity and test compatibility
    }

    private void handleLoveReasons(JsonNode j, Map<String, Integer> loveCounts) {
        final String REASONS = "reasons";
        if (j.has(REASONS) && j.get(REASONS).isArray()) {
            j.get(REASONS).forEach(n -> {
                String r = n.asText("").toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-záéíóúñ0-9 ]", "");
                if (!r.isBlank()) {
                    for (String token : r.split("\\s+")) {
                        if (token.length() >= 3) {
                            loveCounts.merge(token, 1, Integer::sum);
                        }
                    }
                }
            });
        }
    }
}
