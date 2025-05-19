package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.frontend.InfluencerRecommendDto;
import com.scope.backend.scope_api.service.InfluencerRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
@RequiredArgsConstructor
public class InfluencerRecommendController {

    private final InfluencerRecommendService influencerRecommendService;

    @GetMapping("/recommend")
    public List<InfluencerRecommendDto> getRecommendedInfluencers() {
        return influencerRecommendService.getAllInfluencers();
    }
}
