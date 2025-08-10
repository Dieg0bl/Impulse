package com.impulse.api;

import com.impulse.application.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {
    private final ModerationService moderation;
    public ModerationController(ModerationService moderation){this.moderation=moderation;}

    public record ReportPayload(Long targetContentId, String targetContentType, String reason, String description, String url){}
    @PostMapping("/reports")
    public ResponseEntity<?> report(@RequestBody ReportPayload p, Principal principal){
        Long reporter = principal!=null? Long.valueOf(principal.getName()): null;
        long id = moderation.report(reporter, p.targetContentId(), p.targetContentType(), p.reason(), p.description(), p.url());
        return ResponseEntity.status(201).body(Map.of("report_id", id));
    }

    public record ActionPayload(long reportId, String action, String reasonCode, String statement, Long notifiedUserId, Long adminUserId){}
    @PostMapping("/actions")
    public ResponseEntity<?> action(@RequestBody ActionPayload p){
        long id = moderation.action(p.reportId(), p.action(), p.reasonCode(), p.statement(), p.notifiedUserId(), p.adminUserId());
        return ResponseEntity.status(201).body(Map.of("action_id", id));
    }

    public record AppealPayload(long actionId, String message){}
    @PostMapping("/appeals")
    public ResponseEntity<?> appeal(@RequestBody AppealPayload p, Principal principal){
        Long user = principal!=null? Long.valueOf(principal.getName()): null;
        long id = moderation.appeal(p.actionId(), user, p.message());
        return ResponseEntity.status(201).body(Map.of("appeal_id", id));
    }

    public record AppealResolvePayload(String finalStatus, Long adminUserId){}
    @PostMapping("/appeals/{appealId}/resolve")
    public ResponseEntity<?> resolve(@PathVariable long appealId, @RequestBody AppealResolvePayload p){
        moderation.resolveAppeal(appealId, p.finalStatus(), p.adminUserId());
        return ResponseEntity.ok(Map.of("appeal_id", appealId, "status", p.finalStatus()));
    }

    public record TriagePayload(String status){}
    @PostMapping("/reports/{reportId}/triage")
    public ResponseEntity<?> triage(@PathVariable long reportId, @RequestBody TriagePayload p){
        moderation.triage(reportId, p.status());
        return ResponseEntity.ok(Map.of("report_id", reportId, "status", p.status()));
    }

    @GetMapping("/reports")
    public ResponseEntity<java.util.List<Map<String,Object>>> list(@RequestParam(required=false) String status, @RequestParam(required=false) String type, @RequestParam(required=false) Integer limit){
        return ResponseEntity.ok(moderation.listReports(status, type, limit));
    }

    @GetMapping("/reports/stats")
    public ResponseEntity<Map<String,Object>> stats(){
        return ResponseEntity.ok(moderation.reportStats());
    }

    @GetMapping("/actions/{actionId}/statement")
    public ResponseEntity<?> statement(@PathVariable long actionId){
        String st = moderation.fetchStatement(actionId);
        if(st==null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("action_id", actionId, "statement", st));
    }
}
