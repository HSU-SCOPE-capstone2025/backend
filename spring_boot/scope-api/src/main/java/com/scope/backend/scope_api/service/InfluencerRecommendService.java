package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.*;
import com.scope.backend.scope_api.dto.frontend.InfluencerRecommendDto;
import com.scope.backend.scope_api.repository.frontend.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluencerRecommendService {

    private final InfluencerRepository influencerRepository;
    private final InstagramRepository instagramRepository;
    private final YoutubeRepository youtubeRepository;
    private final TiktokRepository tiktokRepository;
    private final AdPriceRepository adPriceRepository;
    private final TotalFollowerRepository totalFollowerRepository;
    private final InstagramCommentRepository instagramCommentRepository;
    private final YoutubeCommentRepository youtubeCommentRepository;
    private final TiktokCommentRepository tiktokCommentRepository;

    public List<InfluencerRecommendDto> getAllInfluencers() {
        List<Influencer> influencers = influencerRepository.findAll();
        return influencers.stream().map(this::buildDto).collect(Collectors.toList());
    }

    private InfluencerRecommendDto buildDto(Influencer influencer) {
        Long influencerNum = influencer.getInfluencerNum();
        Pageable pageable = PageRequest.of(0, 7);

        // ✅ 플랫폼 별 사용자명 조회
        String instaName = instagramRepository.findFirstByInfluencer_InfluencerNumOrderByPostDateDesc(influencerNum)
                .map(Instagram::getUserId).orElse(null);

        String youName = youtubeRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                .map(Youtube::getChannelTitle).orElse(null);

        String tikName = tiktokRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                .map(Tiktok::getUserId).orElse(null);

        // ✅ FSS 평균 계산
        List<Float> instaFssList = instagramRepository.findFSSListByInfluencer(influencerNum);
        float instaFss = instaFssList.isEmpty() ? 0 : (float) instaFssList.stream()
                .mapToDouble(Float::doubleValue).average().orElse(0.0) / 10;

        List<Float> tikFssList = tiktokRepository.findFSSListByInfluencer(influencerNum);
        float tikFss = tikFssList.isEmpty() ? 0 : (float) tikFssList.stream()
                .mapToDouble(Float::doubleValue).average().orElse(0.0) / 10;

        List<Float> youFssList = youtubeRepository.findFSSListByInfluencer(influencerNum);
        float youFss = youFssList.isEmpty() ? 0 : (float) youFssList.stream()
                .mapToDouble(Float::doubleValue).average().orElse(0.0) / 10;

        // ✅ 팔로워 수 조회
        Long instaFollowers = totalFollowerRepository.findTopByInfluencer_InfluencerNumAndPlatformOrderByDateDesc(influencerNum, "Instagram")
                .map(TotalFollower::getSubscriberCount).orElse(0L);

        Long tikFollowers = totalFollowerRepository.findTopByInfluencer_InfluencerNumAndPlatformOrderByDateDesc(influencerNum, "Tiktok")
                .map(TotalFollower::getSubscriberCount).orElse(0L);

        Long youFollowers = totalFollowerRepository.findTopByInfluencer_InfluencerNumAndPlatformOrderByDateDesc(influencerNum, "YouTube")
                .map(TotalFollower::getSubscriberCount).orElse(0L);

        // ✅ 광고 가격 조회
        AdPrice adPrice = adPriceRepository.findByInfluencerNum(influencerNum);

        // ✅ 클러스터링 최다수 추출
        PageRequest pageRequest = PageRequest.of(0, 1);

        // Instagram Cluster
        List<String> instaPostUrls = instagramRepository.findAllByInfluencer_InfluencerNum(influencerNum)
                .stream().map(Instagram::getPostUrl).collect(Collectors.toList());
        String instaCluster = instagramCommentRepository.findTopClusterByPostUrls(instaPostUrls, pageRequest)
                .stream().findFirst().orElse(null);

        // TikTok Cluster
        List<String> tikVideoUrls = tiktokRepository.findAllByInfluencer_InfluencerNum(influencerNum)
                .stream().map(Tiktok::getVideoUrl).collect(Collectors.toList());
        String tikCluster = tiktokCommentRepository.findTopClusterByVideoUrls(tikVideoUrls, pageRequest)
                .stream().findFirst().orElse(null);

        // YouTube Cluster
        List<String> youVideoUrls = youtubeRepository.findAllByInfluencer_InfluencerNum(influencerNum)
                .stream().map(Youtube::getVideoUrl).collect(Collectors.toList());
        String youCluster = youtubeCommentRepository.findTopClusterByVideoUrls(youVideoUrls, pageRequest)
                .stream().findFirst().orElse(null);

        // ✅ DTO 빌딩
        return InfluencerRecommendDto.builder()
                .influencerName(influencer.getInfluencerName())
                .instaName(instaName)
                .youName(youName)
                .tikName(tikName)
                .categories(influencer.getCategories())
                .tag(influencer.getTags())
                .instaFss(instaFss)
                .tikFss(tikFss)
                .youFss(youFss)
                .instaFollowers(instaFollowers)
                .tikFollowers(tikFollowers)
                .youFollowers(youFollowers)
                .instaAd(adPrice.getAdPriceRangeInsta())
                .tikAd(adPrice.getAdPriceRangeTiktok())
                .youAd(adPrice.getAdPriceRangeYoutube())
                .tiktokCommentCluster(tikCluster)
                .youtubeCommentCluster(youCluster)
                .instagramCommentCluster(instaCluster)
                .build();
    }
}
