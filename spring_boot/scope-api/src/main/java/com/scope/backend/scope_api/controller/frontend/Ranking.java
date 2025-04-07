package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.domain.frontend.InfluencersScore;
import com.scope.backend.scope_api.repository.frontend.InfluencerScoreRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class Ranking {

   private final InfluencerScoreRepository influencerScoreRepository;

    public Ranking(InfluencerScoreRepository influencerScoreRepository) {
        this.influencerScoreRepository = influencerScoreRepository;
    }

    @GetMapping("/api/influencers/ranking")
    public List<InfluencersScore> getAllInfluencersScore() {
        return influencerScoreRepository.findAll();
    }





}
