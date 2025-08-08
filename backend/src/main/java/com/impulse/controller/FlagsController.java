package com.impulse.controller;

import java.util.Map;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/flags")
public class FlagsController {

    private final FlagService flagService;

    public FlagsController(FlagService flagService) {
        this.flagService = flagService;
    }

    @GetMapping
    public ResponseEntity<?> all(
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch,
            @RequestHeader(value = HttpHeaders.IF_MODIFIED_SINCE, required = false) String ifModifiedSince) {

        String currentEtag = '"' + flagService.getEtag() + '"';
        long lastModified = flagService.getLastModified();

        if (ifNoneMatch != null && ifNoneMatch.equals(currentEtag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        Map<String, Object> body = flagService.getAll();
        return ResponseEntity.ok()
                .eTag(currentEtag)
                .lastModified(lastModified)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refresh() {
        try {
            flagService.refresh();
            return ResponseEntity.ok(Map.of("refreshed", true, "etag", flagService.getEtag()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("refreshed", false, "error", e.getMessage()));
        }
    }
}

