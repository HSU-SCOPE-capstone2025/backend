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
import java.util.Objects;

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

        SnsDto sns = new SnsDto(instagram, youtube, tiktok);
        return new InfluencerSnsSummaryDto(
                influencer.getName(),
                instaId,
                youId,
                tikId,
                tags,
                categories,
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
                .map(o -> o[0].toString())
                .orElse("Unknown");
    }

    private List<DailyStatDto> getDailyStats(List<? extends Object> entities, int limit) {
        return entities.stream()
                .sorted(Comparator.comparing(e -> {
                    if (e instanceof Instagram i) return i.getPostDate();
                    if (e instanceof Tiktok t) return t.getUploadDate();
                    if (e instanceof Youtube y) return y.getUploadDate();
                    return null;
                }, Comparator.reverseOrder()))
                .limit(limit)
                .map(e -> {
                    if (e instanceof Instagram i) {
                        return new DailyStatDto(i.getPostDate().toString(), null, (long) i.getLikeCount(), (long) i.getCommentCount(), null);
                    } else if (e instanceof Tiktok t) {
                        return new DailyStatDto(t.getUploadDate().toString(), t.getFollowerNum(), t.getLikeCount(), t.getCommentCount(), t.getViewCount());
                    } else if (e instanceof Youtube y) {
                        return new DailyStatDto(y.getUploadDate().toString(), y.getSubscriberCount(), y.getLikeCount(), y.getCommentCount(), y.getViewCount());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
