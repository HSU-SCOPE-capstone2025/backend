package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.Instagram;
import com.scope.backend.scope_api.domain.frontend.Tiktok;
import com.scope.backend.scope_api.domain.frontend.Youtube;
import com.scope.backend.scope_api.dto.detailsns.*;
import com.scope.backend.scope_api.repository.frontend.*;
import com.scope.backend.scope_api.service.instagram.InfluencerSnsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InfluencerSnsServiceImpl implements InfluencerSnsService {

    private final InstagramRepository instagramRepository;
    private final InstagramCommentRepository instagramCommentRepository;
    private final TiktokRepository tiktokRepository;
    private final TiktokCommentRepository tiktokCommentRepository;
    private final YoutubeRepository youtubeRepository;
    private final YoutubeCommentRepository youtubeCommentRepository;
    private final TotalFollowerRepository totalFollowerRepository;
    private final AdPriceRepository adPriceRepository;

    private static final Map<String, String> CLUSTER_KOR_MAP = Map.of(
            "Aggressive", "공격적인",
            "Supportive", "지지하는",
            "Neutral Informative", "정보제공형",
            "Playful", "유쾌함",
            "Analytical", "분석적",
            "Spam/Promotional", "스팸",
            "Empathetic", "공감하는",
            "Sarcastic/Playful", "쾌활함"
    );

    @Override
    public InfluencerSnsSummaryDto getSnsSummary(String userId) {
        Instagram insta = instagramRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 인플루언서 없음"));
        Influencer influencer = insta.getInfluencer();
        Long influencerNum = influencer.getInfluencerNum();

        // ✅ 플랫폼별 ID 추출
        String instaId = insta.getUserId();
        String tikId = tiktokRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                .map(Tiktok::getUserId).orElse(null);
        String youId = youtubeRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                .map(Youtube::getChannelTitle).orElse(null);

        String tags = influencer.getTags();
        String categories = influencer.getCategories();

        PlatformSummaryDto instagram = makeInstagramDto(influencerNum, userId);
        PlatformSummaryDto tiktok = makeTiktokDto(influencerNum, userId);
        PlatformSummaryDto youtube = makeYoutubeDto(influencerNum, userId);

        FollowerHistoryDto followerHistory = new FollowerHistoryDto(
                getFollowerHistoryList(influencerNum, "Instagram"),
                getFollowerHistoryList(influencerNum, "Tiktok"),
                getFollowerHistoryList(influencerNum, "YouTube")
        );

        SnsDto sns = new SnsDto(instagram, youtube, tiktok);
        return new InfluencerSnsSummaryDto(
                influencer.getName(),
                instaId,
                youId,
                tikId,
                tags,
                categories,
                followerHistory, // ✅ followers 필드에 넣기
                sns
        );
    }

    private PlatformSummaryDto makeInstagramDto(Long influencerNum, String userId) {
        Long followers = getLatestFollower(influencerNum, "Instagram");
        Float fss = average(instagramRepository.findFSSListByInfluencer(influencerNum));
        Float avgLikes = average(instagramRepository.findLikeListByInfluencer(influencerNum));
        Float avgComments = average(instagramRepository.findCommentCountListByInfluencer(influencerNum));
        String cluster = findTopCluster(instagramCommentRepository.countClusterByInfluencer(influencerNum));
        String url = instagramRepository.findFirstByInfluencer_InfluencerNum(influencerNum)
                .map(Instagram::getInfluencerUrl)
                .orElse(null);
        String adCost = adPriceRepository.findByInfluencerNum(influencerNum).getAdPriceInsta();

        List<DailyStatDto> dailyStats = getDailyStats(instagramRepository.findAllByInfluencer_InfluencerNum(influencerNum), 21);

        return new PlatformSummaryDto(
                followers,
                avgLikes.longValue(),
                avgComments.longValue(),
                null,
                fss,
                cluster,
                url,
                adCost,
                dailyStats,
                null
        );
    }

    private PlatformSummaryDto makeTiktokDto(Long influencerNum, String userId) {
        Long followers = getLatestFollower(influencerNum, "Tiktok");
        Float fss = average(tiktokRepository.findFSSListByInfluencer(influencerNum));
        Float avgLikes = average(tiktokRepository.findLikeListByInfluencer(influencerNum));
        Float avgComments = average(tiktokRepository.findCommentCountListByInfluencer(influencerNum));
        Float avgViews = average(tiktokRepository.findViewListByInfluencer(influencerNum));
        String cluster = findTopCluster(tiktokCommentRepository.countClusterByInfluencer(influencerNum));
        String url = tiktokRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                .map(Tiktok::getInfluencerUrl)
                .orElse(null);

        String adCost = adPriceRepository.findByInfluencerNum(influencerNum).getAdPriceTiktok();

        List<DailyStatDto> dailyStats = getDailyStats(tiktokRepository.findAllByInfluencer_InfluencerNum(influencerNum), 21);

        return new PlatformSummaryDto(
                followers,
                avgLikes.longValue(),
                avgComments.longValue(),
                avgViews.longValue(),
                fss,
                cluster,
                url,
                adCost,
                dailyStats,
                null
        );
    }

    private PlatformSummaryDto makeYoutubeDto(Long influencerNum, String userId) {
        Long followers = getLatestFollower(influencerNum, "YouTube");
        Float fss = average(youtubeRepository.findFSSListByInfluencer(influencerNum));
        Float avgLikes = average(youtubeRepository.findLikeListByInfluencer(influencerNum));
        Float avgComments = average(youtubeRepository.findCommentCountListByInfluencer(influencerNum));
        Float avgViews = average(youtubeRepository.findViewListByInfluencer(influencerNum));
        String cluster = findTopCluster(youtubeCommentRepository.countClusterByInfluencer(influencerNum));
        String url = youtubeRepository.findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(influencerNum)
                .map(Youtube::getChannelUrl)
                .orElse(null);

        String adCost = adPriceRepository.findByInfluencerNum(influencerNum).getAdPriceYoutube();

        List<DailyStatDto> dailyStats = getDailyStats(youtubeRepository.findAllByInfluencer_InfluencerNum(influencerNum), 21);
        List<YoutubeThumbnailDto> thumbnails = youtubeRepository.findYoutubeThumbnailsByInfluencer(influencerNum, PageRequest.of(0, 20))
                .stream()
                .map(map -> new YoutubeThumbnailDto(map.get("date").toString(), map.get("url").toString()))
                .toList();

        return new PlatformSummaryDto(
                followers,
                avgLikes.longValue(),
                avgComments.longValue(),
                avgViews.longValue(),
                fss,
                cluster,
                url,
                adCost,
                dailyStats,
                thumbnails
        );
    }

    private Long getLatestFollower(Long influencerNum, String platform) {
        return totalFollowerRepository.findLatestFollowerCount(influencerNum, platform).stream().findFirst().orElse(0L);
    }

    private Float average(List<Float> list) {
        return list.isEmpty() ? 0f : (float) list.stream().mapToDouble(Float::doubleValue).average().orElse(0);
    }

    private String findTopCluster(List<Object[]> counts) {
        return counts.stream()
                .max(Comparator.comparingLong(o -> (Long) o[1]))
                .map(o -> {
                    String eng = o[0].toString();
                    return CLUSTER_KOR_MAP.getOrDefault(eng, eng); // 한글 매핑
                })
                .orElse("Unknown");
    }

    private List<DailyStatDto> getDailyStats(List<? extends Object> entities, int limit) {
        // 그룹핑 및 합산
        Map<String, DailyStatDto> grouped = entities.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    String date = null;
                    Long likes = 0L, comments = 0L, views = null;

                    if (e instanceof Instagram i) {
                        date = i.getPostDate().toString();
                        likes = (long) i.getLikeCount();
                        comments = i.getCommentCount();
                    } else if (e instanceof Tiktok t) {
                        date = t.getUploadDate().toString();
                        likes = t.getLikeCount();
                        comments = t.getCommentCount();
                        views = t.getViewCount();
                    } else if (e instanceof Youtube y) {
                        date = y.getUploadDate().toString();
                        likes = y.getLikeCount();
                        comments = y.getCommentCount();
                        views = y.getViewCount();
                    }

                    return new DailyStatDto(date, likes, comments, views);
                })
                .collect(Collectors.toMap(
                        DailyStatDto::getDate,
                        d -> d,
                        (d1, d2) -> new DailyStatDto(
                                d1.getDate(),
                                d1.getLikes() + d2.getLikes(),
                                d1.getComments() + d2.getComments(),
                                sumNullable(d1.getViews(), d2.getViews())
                        )
                ));

        // 정렬 후 limit 개수만 리스트로 반환
        return grouped.values().stream()
                .sorted(Comparator.comparing(DailyStatDto::getDate).reversed())
                .limit(limit)
                .toList();
    }

    // 🔧 null-safe summation for views
    private Long sumNullable(Long a, Long b) {
        if (a == null) return b;
        if (b == null) return a;
        return a + b;
    }


    private List<FollowerDataDto> getFollowerHistoryList(Long influencerNum, String platform) {
        return totalFollowerRepository.findAllFollowerHistory(influencerNum, platform).stream()
                .map(map -> new FollowerDataDto(map.get("date").toString(), (Long) map.get("followers")))
                .toList();
    }
}
