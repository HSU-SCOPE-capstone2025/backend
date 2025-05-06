package com.scope.backend.scope_api.dto.crawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InfluencerFollowerDto {
    private String influencer;
    @JsonProperty("follower_number")
    private int followerNumber;

    @JsonProperty("at_time")
    private LocalDate atTime;
}
