package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.FeedbackService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedback;
    private final FlagService flags;
    public FeedbackController(FeedbackService feedback, FlagService flags){this.feedback=feedback;this.flags=flags;}

    @PostMapping("/nps/{userId}")
    public ResponseEntity<?> nps(@PathVariable Long userId, @RequestParam int score, @RequestParam(required=false) String reason){
    if(!flags.isOn("support.nps")) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(feedback.submitNps(userId, score, reason));
    }

    @PostMapping("/csat/{userId}")
    public ResponseEntity<?> csat(@PathVariable Long userId, @RequestParam int score){
    if(!flags.isOn("support.csat")) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(feedback.submitCsat(userId, score));
    }

    @GetMapping("/nps/aggregate")
    public ResponseEntity<?> npsAgg(){
    if(!flags.isOn("support.nps")) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(feedback.npsAggregate());
    }

    @GetMapping("/csat/aggregate")
    public ResponseEntity<?> csatAgg(){
    if(!flags.isOn("support.csat")) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(feedback.csatAggregate());
    }
}
