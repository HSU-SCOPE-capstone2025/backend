package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.Instagram;
import com.scope.backend.scope_api.domain.frontend.Tiktok;
import com.scope.backend.scope_api.domain.frontend.Youtube;
import com.scope.backend.scope_api.dto.frontend.InfluencerSearchResponse;
import com.scope.backend.scope_api.repository.frontend.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfluencerSearchService {

    @Autowired
    private InfluencerRepository influencerRepository;
    @Autowired
    private InstagramRepository instagramRepository;
    @Autowired
    private TiktokRepository tiktokRepository;
    @Autowired
    private YoutubeRepository youtubeRepository;
    @Autowired
    private TotalFollowerRepository totalFollowerRepository;

    public List<InfluencerSearchResponse> getSearchResults() {
        return influencerRepository.findAll().stream().map(influencer -> {
            Long num = influencer.getInfluencerNum();

            // ✅ SNS별 이름
            String instaName = instagramRepository.findFirstByInfluencer_InfluencerNumOrderByPostDateDesc(num)
                    .map(Instagram::getUserId).orElse(null);
            String youName = youtubeRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(num)
                    .map(Youtube::getChannelTitle).orElse(null);
            String tikName = tiktokRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(num)
                    .map(Tiktok::getUserId).orElse(null);

            // 평균 댓글 계산
            Float instaAvgComment = avg(instagramRepository.findCommentCountListByInfluencer(num));
            Float tikAvgComment = avg(tiktokRepository.findCommentCountListByInfluencer(num));
            Float youAvgComment = avg(youtubeRepository.findCommentCountListByInfluencer(num));

            return InfluencerSearchResponse.builder()
                    .name(influencer.getName())
                    .instaName(instaName)
                    .youName(youName)
                    .tikName(tikName)
                    .tags(influencer.getTags())
                    .categories(influencer.getCategories())
                    .instaFss(getFss(instagramRepository.findFSSListByInfluencer(num)))
                    .instaFollowers(getFollower("Instagram", num))
                    .instaAverageLikes(avg(instagramRepository.findLikeListByInfluencer(num)))
                    .instaAverageViews(0f)
                    .instaAverageComments(instaAvgComment)
                    .tikFss(getFss(tiktokRepository.findFSSListByInfluencer(num)))
                    .tikFollowers(getFollower("Tiktok", num))
                    .tikAverageLikes(avg(tiktokRepository.findLikeListByInfluencer(num)))
                    .tikAverageViews(avg(tiktokRepository.findViewListByInfluencer(num)))
                    .tikAverageComments(tikAvgComment)
                    .youFss(getFss(youtubeRepository.findFSSListByInfluencer(num)))
                    .youFollowers(getFollower("YouTube", num))
                    .youAverageLikes(avg(youtubeRepository.findLikeListByInfluencer(num)))
                    .youAverageViews(avg(youtubeRepository.findViewListByInfluencer(num)))
                    .youAverageComments(youAvgComment)
                    .build();
        }).collect(Collectors.toList());
    }

    private Float avg(List<Float> list) {
        return list.isEmpty() ? 0f : (float) list.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
    }

    private Float getFss(List<Float> list) {
        return list.isEmpty() ? 0f : avg(list) / 10;
    }

    private Long getFollower(String platform, Long num) {
        return totalFollowerRepository.findLatestFollowerCount(num, platform).stream().findFirst().orElse(0L);
    }
}
