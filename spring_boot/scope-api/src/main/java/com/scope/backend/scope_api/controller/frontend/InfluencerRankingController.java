package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.frontend.InfluencerRankingResponse;
import com.scope.backend.scope_api.dto.frontend.InfluencerSearchResponse;
import com.scope.backend.scope_api.service.InfluencerSearchService;
import com.scope.backend.scope_api.service.instagram.InfluencerRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerRankingController {

    @Autowired
    private InfluencerRankingService influencerRankingService;

    @Autowired
    private InfluencerSearchService influencerSearchService;

    @GetMapping("/ranking")
    public List<InfluencerRankingResponse> getTopInfluencers() {
        return influencerRankingService.getTopInfluencers();
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search() {
        List<InfluencerSearchResponse> result = influencerSearchService.getSearchResults();

        Map<String, Object> response = new HashMap<>();
        response.put("influencers", result);

        return ResponseEntity.ok(response);
    }
}
