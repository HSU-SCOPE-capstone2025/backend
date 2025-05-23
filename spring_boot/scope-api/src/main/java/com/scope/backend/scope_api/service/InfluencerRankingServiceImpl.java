package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.Instagram;
import com.scope.backend.scope_api.domain.frontend.Tiktok;
import com.scope.backend.scope_api.domain.frontend.Youtube;
import com.scope.backend.scope_api.dto.frontend.InfluencerRankingResponse;
import com.scope.backend.scope_api.repository.frontend.*;
import com.scope.backend.scope_api.service.instagram.InfluencerRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfluencerRankingServiceImpl implements InfluencerRankingService {

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

    @Override
    public List<InfluencerRankingResponse> getTopInfluencers() {
        // 1️⃣ 모든 Influencer 가져오기
        List<Influencer> influencers = influencerRepository.findAll();


        // 2️⃣ 각 Influencer 정보 매핑
        return influencers.stream().map(influencer -> {
            Long influencerNum = influencer.getInfluencerNum();

            // ✅ 플랫폼 별 사용자명 조회
            String instaName = instagramRepository.findFirstByInfluencer_InfluencerNumOrderByPostDateDesc(influencerNum)
                    .map(Instagram::getUserId).orElse(null);

            String youName = youtubeRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                    .map(Youtube::getChannelTitle).orElse(null);

            String tikName = tiktokRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                    .map(Tiktok::getUserId).orElse(null);

            // 🔹 Instagram 데이터 조회
            List<Float> instaFssList = instagramRepository.findFSSListByInfluencer(influencerNum);
            Float instaFss = instaFssList.isEmpty() ? 0 : (float) instaFssList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
            instaFss /= 10;

            List<Float> instaLikeList = instagramRepository.findLikeListByInfluencer(influencerNum);
            Float instaAverageLikes = instaLikeList.isEmpty() ? 0 : (float) instaLikeList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);

            Long instaFollowers = totalFollowerRepository.findLatestFollowerCount(influencerNum, "Instagram")
                    .stream().findFirst().orElse(0L);

            // 🔹 Tiktok 데이터 조회
            List<Float> tikFssList = tiktokRepository.findFSSListByInfluencer(influencerNum);
            Float tikFss = tikFssList.isEmpty() ? 0 : (float) tikFssList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
            tikFss /= 10;

            List<Float> tikLikeList = tiktokRepository.findLikeListByInfluencer(influencerNum);
            Float tikAverageLikes = tikLikeList.isEmpty() ? 0 : (float) tikLikeList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);

            List<Float> tikViewList = tiktokRepository.findViewListByInfluencer(influencerNum);
            Float tikAverageViews = tikViewList.isEmpty() ? 0 : (float) tikViewList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);

            Long tikFollowers = totalFollowerRepository.findLatestFollowerCount(influencerNum, "Tiktok")
                    .stream().findFirst().orElse(0L);

            // 🔹 Youtube 데이터 조회
            List<Float> youFssList = youtubeRepository.findFSSListByInfluencer(influencerNum);
            Float youFss = youFssList.isEmpty() ? 0 : (float) youFssList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
            youFss /= 10;

            List<Float> youLikeList = youtubeRepository.findLikeListByInfluencer(influencerNum);
            Float youAverageLikes = youLikeList.isEmpty() ? 0 : (float) youLikeList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);

            List<Float> youViewList = youtubeRepository.findViewListByInfluencer(influencerNum);
            Float youAverageViews = youViewList.isEmpty() ? 0 : (float) youViewList.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);

            Long youFollowers = totalFollowerRepository.findLatestFollowerCount(influencerNum, "YouTube")
                    .stream().findFirst().orElse(0L);

            // 🔹 DTO 생성
            return InfluencerRankingResponse.builder()
                    .name(influencer.getName())
                    .instaName(instaName)
                    .youName(youName)
                    .tikName(tikName)
                    .tags(influencer.getTags())
                    .categories(influencer.getCategories())
                    .instaFss(instaFss)
                    .instaFollowers(instaFollowers)
                    .instaAverageLikes(instaAverageLikes)
                    .instaAverageViews(0) // Instagram은 조회수 0
                    .tikFss(tikFss)
                    .tikFollowers(tikFollowers)
                    .tikAverageLikes(tikAverageLikes)
                    .tikAverageViews(tikAverageViews)
                    .youFss(youFss)
                    .youFollowers(youFollowers)
                    .youAverageLikes(youAverageLikes)
                    .youAverageViews(youAverageViews)
                    .build();
        }).collect(Collectors.toList());
    }
}
