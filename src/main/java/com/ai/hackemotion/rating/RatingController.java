package com.ai.hackemotion.rating;

import com.ai.hackemotion.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingRepository repository;

    public RatingController(RatingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RatingResponse> getRatings() {
        return repository.findAllByOrderByScoreDesc()
                .stream()
                .map(user -> new RatingResponse(user.getRank(), user.getUser().getUsername(), user.getScore()))
                .toList();
    }

    record RatingResponse(int rank, String user, int score) {}
}
