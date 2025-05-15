package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.frontend.InfluencerRankingResponse;
import com.scope.backend.scope_api.service.instagram.InfluencerRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerRankingController {

    @Autowired
    private InfluencerRankingService influencerRankingService;

    @GetMapping("/ranking")
    public List<InfluencerRankingResponse> getTopInfluencers() {
        return influencerRankingService.getTopInfluencers();
    }
}
