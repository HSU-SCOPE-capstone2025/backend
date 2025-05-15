package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.*;
import com.scope.backend.scope_api.dto.frontend.InfluencerDTO;
import com.scope.backend.scope_api.repository.frontend.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerRepository influencerRepository;
    private final InstagramPostRepository instagramPostRepository;
    private final TiktokVideoRepository tiktokVideoRepository;
    private final YoutubeVideoRepository youtubeVideoRepository;
    private final TotalFollowersRepository totalFollowersRepository;

    // 30명의 인플루언서 랭킹 데이터 가져오기
    public List<InfluencerDTO> getTop30Influencers() {
        Page<Influencer> influencerPage = influencerRepository.findTop30Influencers(PageRequest.of(0, 30));

        List<Influencer> influencers = influencerPage.getContent();

        return influencers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 각 Influencer 정보를 DTO로 변환
    private InfluencerDTO convertToDTO(Influencer influencer) {

        // 💡 1️⃣ Instagram 데이터 가져오기
        List<InstagramPost> instagramPosts = instagramPostRepository
                .findTop7ByInfluencerOrderByAtTimeDesc(influencer);

        int instaFollowers = totalFollowersRepository
                .findInstagramFollowers(influencer)
                .map(TotalFollowers::getSubscriberCount)
                .orElse(0);

        // 평균 계산 (likeCount, viewCount)
        double instaAverageViews = instagramPosts.stream()
                .mapToInt(InstagramPost::getFollowerNum)
                .average()
                .orElse(0);

        double instaAverageLikes = instagramPosts.stream()
                .mapToInt(InstagramPost::getLikeCount)
                .average()
                .orElse(0);

        // 💡 2️⃣ TikTok 데이터 가져오기
        List<TiktokVideo> tiktokVideos = tiktokVideoRepository
                .findTop7ByInfluencerOrderByUploadDateDesc(influencer);

        int tikFollowers = totalFollowersRepository
                .findTiktokFollowers(influencer)
                .map(TotalFollowers::getSubscriberCount)
                .orElse(0);

        double tikAverageViews = tiktokVideos.stream()
                .mapToInt(TiktokVideo::getViewCount)
                .average()
                .orElse(0);

        double tikAverageLikes = tiktokVideos.stream()
                .mapToInt(TiktokVideo::getLikeCount)
                .average()
                .orElse(0);

        // 💡 3️⃣ YouTube 데이터 가져오기
        List<YoutubeVideo> youtubeVideos = youtubeVideoRepository
                .findTop7ByInfluencerOrderByUploadDateDesc(influencer);

        int youFollowers = totalFollowersRepository
                .findYoutubeFollowers(influencer)
                .map(TotalFollowers::getSubscriberCount)
                .orElse(0);

        double youAverageViews = youtubeVideos.stream()
                .mapToInt(YoutubeVideo::getViewCount)
                .average()
                .orElse(0);

        double youAverageLikes = youtubeVideos.stream()
                .mapToInt(YoutubeVideo::getLikeCount)
                .average()
                .orElse(0);

        // 💡 4️⃣ DTO 생성
        return new InfluencerDTO(
                influencer.getName(),
                influencer.getCategories(),
                instaFollowers,
                instaAverageViews,
                instaAverageLikes,
                tikFollowers,
                tikAverageViews,
                tikAverageLikes,
                youFollowers,
                youAverageViews,
                youAverageLikes
        );
    }
}
