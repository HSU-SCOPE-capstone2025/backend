package com.scope.backend.scope_api.dto.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerRankingDTO {

    private String name;
    private List<String> tags;
    private List<String> categories;

    private int instaFss;
    private int tikFss;
    private int youFss;

    private long instaFollowers;
    private long instaAverageViews;
    private long instaAverageLikes;

    private long tikFollowers;
    private long tikAverageViews;
    private long tikAverageLikes;

    private long youFollowers;
    private long youAverageViews;
    private long youAverageLikes;
}
