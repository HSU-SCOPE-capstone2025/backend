package com.scope.backend.scope_api.dto.frontend;

import lombok.Getter;

@Getter
public class InfluencerSearchDto {
    private String name;
    private int followers;
    private int averageViews;
    private int averageComments;
    private int averageLikes;

    public InfluencerSearchDto(String name, int followers, int averageViews, int averageComments, int averageLikes) {
        this.name = name;
        this.followers = followers;
        this.averageViews = averageViews;
        this.averageComments = averageComments;
        this.averageLikes = averageLikes;
    }

}
