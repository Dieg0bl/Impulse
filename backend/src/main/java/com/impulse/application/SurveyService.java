package com.impulse.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impulse.domain.pmf.Survey;
import com.impulse.infrastructure.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurveyService {
    private final SurveyRepository repo;
    public SurveyService(SurveyRepository repo){ this.repo = repo; }
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
        double very = 0, totalSE = 0, npsSum = 0, npsCount = 0;
        Map<String, Integer> loveCounts = new HashMap<>();

        for (Survey s : repo.findAll()) {
            try {
                JsonNode j = om.readTree(s.getAnswersJson());
                switch (s.getSurveyType()) {
                    case "sean_ellis" -> {
                        String ans = j.path("q1_howFeelIfDisappears").asText("");
                        if (!ans.isEmpty()) {
                            totalSE++;
                            if ("very_disappointed".equalsIgnoreCase(ans)) very++;
                        }
                    }
                    case "nps" -> {
                        int score = j.path("score").asInt(-1);
                        if (score >= 0) {
                            npsSum += score;
                            npsCount++;
                        }
                    }
                    case "love_reasons" -> {
                        if (j.has("reasons") && j.get("reasons").isArray()) {
                            j.get("reasons").forEach(n -> {
                                String r = n.asText("").toLowerCase(Locale.ROOT)
                                        .replaceAll("[^a-záéíóúñ0-9 ]", "");
                                if (!r.isBlank()) {
                                    for (String token : r.split("\\s+")) if (token.length() >= 3)
                                        loveCounts.merge(token, 1, Integer::sum);
                                }
                            });
                        }
                    }
                }
            } catch (Exception ignored) { }
        }
        List<String> top = loveCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10).map(Map.Entry::getKey).collect(Collectors.toList());

        double veryPct = totalSE == 0 ? 0 : (very / totalSE * 100.0);
        double npsAvg = npsCount == 0 ? 0 : (npsSum / npsCount);
        long weeklyAha = 0; // delegado a AnalyticsService
        return new PmfSignals(veryPct, npsAvg, top, weeklyAha);
    }
}
