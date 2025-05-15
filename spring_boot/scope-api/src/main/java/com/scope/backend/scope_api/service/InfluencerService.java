package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.*;
import com.scope.backend.scope_api.dto.frontend.InfluencerRankingDTO;
import com.scope.backend.scope_api.repository.frontend.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InfluencerService {

    private final InfluencerRepository influencerRepository;
    private final InstagramRepository instagramRepository;
    private final InstagramCommentRepository instagramCommentRepository;
    private final TikTokRepository tikTokRepository;
    private final TikTokCommentRepository tikTokCommentRepository;
    private final YouTubeRepository youTubeRepository;
    private final YouTubeCommentRepository youTubeCommentRepository;

    public List<InfluencerRankingDTO> getInfluencerRankings() {
        List<Influencer> influencers = influencerRepository.findAll();

        return influencers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private InfluencerRankingDTO convertToDTO(Influencer influencer) {
        // Instagram 정보 가져오기
        List<Instagram> instagrams = instagramRepository.findRecentPosts(influencer.getId());
        long instaViews = instagrams.stream().mapToLong(Instagram::getViews).sum() / instagrams.size();
        long instaLikes = instagrams.stream().mapToLong(Instagram::getLikes).sum() / instagrams.size();

        // Instagram FSS 계산
        int instaFss = (int) instagrams.stream()
                .flatMap(insta -> instagramCommentRepository.findRecentComments(insta.getId()).stream())
                .mapToInt(InstagramComment::getFfs)
                .average().orElse(0);

        // TikTok 정보 가져오기
        List<TikTok> tikToks = tikTokRepository.findRecentVideos(influencer.getId());
        long tikViews = tikToks.stream().mapToLong(TikTok::getViews).sum() / tikToks.size();
        long tikLikes = tikToks.stream().mapToLong(TikTok::getLikes).sum() / tikToks.size();

        // TikTok FSS 계산
        int tikFss = (int) tikToks.stream()
                .flatMap(tik -> tikTokCommentRepository.findRecentComments(tik.getId()).stream())
                .mapToInt(TikTokComment::getFfs)
                .average().orElse(0);

        // YouTube 정보 가져오기
        List<YouTube> youTubes = youTubeRepository.findRecentVideos(influencer.getId());
        long youViews = youTubes.stream().mapToLong(YouTube::getViews).sum() / youTubes.size();
        long youLikes = youTubes.stream().mapToLong(YouTube::getLikes).sum() / youTubes.size();

        // YouTube FSS 계산
        int youFss = (int) youTubes.stream()
                .flatMap(yt -> youTubeCommentRepository.findRecentComments(yt.getId()).stream())
                .mapToInt(YouTubeComment::getFfs)
                .average().orElse(0);

        // DTO로 변환
        return InfluencerRankingDTO.builder()
                .name(influencer.getName())
                .tags(influencer.getTags())
                .categories(influencer.getCategories())
                .instaFss(instaFss)
                .tikFss(tikFss)
                .youFss(youFss)
                .instaFollowers(influencer.getInstaFollowers())
                .instaAverageViews(instaViews)
                .instaAverageLikes(instaLikes)
                .tikFollowers(influencer.getTikFollowers())
                .tikAverageViews(tikViews)
                .tikAverageLikes(tikLikes)
                .youFollowers(influencer.getYouFollowers())
                .youAverageViews(youViews)
                .youAverageLikes(youLikes)
                .build();
    }
}
