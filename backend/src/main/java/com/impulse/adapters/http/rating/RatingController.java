package com.impulse.adapters.http.rating;

import com.impulse.application.rating.dto.RatingResponse;
import com.impulse.application.rating.dto.CreateRatingCommand;
import com.impulse.application.rating.usecase.CreateRatingUseCase;
import com.impulse.application.rating.usecase.GetRatingByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final CreateRatingUseCase createRatingUseCase;
    private final GetRatingByIdUseCase getRatingByIdUseCase;

    public RatingController(CreateRatingUseCase createRatingUseCase, GetRatingByIdUseCase getRatingByIdUseCase) {
        this.createRatingUseCase = createRatingUseCase;
        this.getRatingByIdUseCase = getRatingByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@RequestBody CreateRatingCommand command) {
        RatingResponse response = createRatingUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponse> getRatingById(@PathVariable Long id) {
        RatingResponse response = getRatingByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
