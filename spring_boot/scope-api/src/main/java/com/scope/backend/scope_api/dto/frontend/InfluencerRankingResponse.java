package com.scope.backend.scope_api.dto.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfluencerRankingResponse {
    private String name;
    private String tags;
    private String categories;

    // Instagram 정보
    private Float instaFss;
    private Long instaFollowers;
    private Float instaAverageLikes;
    private Integer instaAverageViews = 0; // Instagram은 조회수 0으로 설정

    // Tiktok 정보
    private Float tikFss;
    private Long tikFollowers;
    private Float tikAverageLikes;
    private Float tikAverageViews;

    // Youtube 정보
    private Float youFss;
    private Long youFollowers;
    private Float youAverageLikes;
    private Float youAverageViews;
}
