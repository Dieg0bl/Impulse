package com.impulse.experiments;

// Using constructor injection for ExperimentEngine
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experiments")
public class ExperimentController {
    private final ExperimentEngine engine;

    public ExperimentController(ExperimentEngine engine){
        this.engine = engine;
    }

    @GetMapping("/{experiment}/{userId}")
    public String variant(@PathVariable String experiment, @PathVariable long userId){
        return engine.variant(experiment, userId);
    }
}
