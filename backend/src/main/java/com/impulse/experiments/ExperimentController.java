package com.impulse.experiments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentController {
    @Autowired private ExperimentEngine engine;

    @GetMapping("/{experiment}/{userId}")
    public String variant(@PathVariable String experiment, @PathVariable long userId){
        return engine.variant(experiment, userId);
    }
}
