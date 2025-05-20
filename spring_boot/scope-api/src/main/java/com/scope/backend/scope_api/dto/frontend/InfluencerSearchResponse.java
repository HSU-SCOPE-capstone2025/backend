package com.scope.backend.scope_api.dto.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfluencerSearchResponse {
    private String name;
    private String tags;
    private String categories;

    private Float instaFss;
    private Long instaFollowers;
    private Float instaAverageLikes;
    private Float instaAverageViews;
    private Float instaAverageComments;

    private Float tikFss;
    private Long tikFollowers;
    private Float tikAverageLikes;
    private Float tikAverageViews;
    private Float tikAverageComments;

    private Float youFss;
    private Long youFollowers;
    private Float youAverageLikes;
    private Float youAverageViews;
    private Float youAverageComments;
}
