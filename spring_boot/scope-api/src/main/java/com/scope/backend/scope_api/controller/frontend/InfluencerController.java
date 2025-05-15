package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.frontend.InfluencerRankingDTO;
import com.scope.backend.scope_api.service.InfluencerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
@RequiredArgsConstructor
public class InfluencerController {

    private final InfluencerService influencerService;

    @GetMapping("/ranking")
    public ResponseEntity<List<InfluencerRankingDTO>> getInfluencerRankings() {
        List<InfluencerRankingDTO> rankings = influencerService.getInfluencerRankings();
        return ResponseEntity.ok(rankings);
    }
}
