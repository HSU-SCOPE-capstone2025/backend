package com.scope.backend.scope_api.dto.frontend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluencerDTO {
    private String name;
    private String categories;
    private int instaFollowers;
    private double instaAverageViews;
    private double instaAverageLikes;
    private int tikFollowers;
    private double tikAverageViews;
    private double tikAverageLikes;
    private int youFollowers;
    private double youAverageViews;
    private double youAverageLikes;
}
