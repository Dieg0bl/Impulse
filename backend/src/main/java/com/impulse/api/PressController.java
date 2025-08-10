package com.impulse.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impulse.application.PressService;
import com.impulse.common.flags.FlagService;

@RestController
@RequestMapping("/api/press")
public class PressController {
    private final PressService press;
    private final FlagService flags;
    public PressController(PressService press, FlagService flags){this.press=press;this.flags=flags;}

    private boolean enabled(){return flags.isOn("press.live_counter");}

    @GetMapping("/live")
    public ResponseEntity<?> live(){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Map.of("value", press.current()));
    }

    @PostMapping("/live/increment/{delta}")
    public ResponseEntity<?> inc(@PathVariable long delta){
        if(!enabled()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Map.of("value", press.increment(delta)));
    }
}
