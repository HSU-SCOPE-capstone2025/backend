package com.scope.backend.scope_api.dto.frontend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfluencerRecommendDto {
    private String influencerName;
    private String instaName;
    private String youName;
    private String tikName;
    private String categories;
    private String tag;
    private float instaFss;
    private float tikFss;
    private float youFss;
    private Long instaFollowers;
    private Long tikFollowers;
    private Long youFollowers;
    private String tikAd;
    private String youAd;
    private String instaAd;
    private String tiktokCommentCluster;
    private String youtubeCommentCluster;
    private String instagramCommentCluster;
}
