package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.detail.InfluencerAnalysisResponse;
import com.scope.backend.scope_api.dto.frontend.InfluencerSimpleDto;
import com.scope.backend.scope_api.service.InfluencerSimpleService;
import com.scope.backend.scope_api.service.instagram.InfluencerAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/influencers")
@RequiredArgsConstructor
public class InfluencerDetailController {

    private final InfluencerSimpleService influencerSimpleService;

    private final InfluencerAnalysisService influencerAnalysisService;

    @GetMapping("/detail/{userId}")
    public ResponseEntity<InfluencerSimpleDto> getSimpleDetail(@PathVariable("userId") String userId) {
        InfluencerSimpleDto dto = influencerSimpleService.getByInstagramUserId(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/detail/{id}/analysis")
    public ResponseEntity<InfluencerAnalysisResponse> getAnalysis(@PathVariable String id) {
        return ResponseEntity.ok(influencerAnalysisService.getAnalysisByUserId(id));
    }
}
