package com.scope.backend.scope_api.dto.detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerAnalysisResponse {
    private String name;
    private PlatformScoresDto platformScores;

    private Long instaFollowers;
    private Double instaHighFssRatio;

    private Long youFollowers;
    private Double youHighFssRatio;

    private Long tikFollowers;
    private Double tikHighFssRatio;

    private PlatformAnalysisDto instagram;
    private PlatformAnalysisDto youtube;
    private PlatformAnalysisDto tiktok;
}
