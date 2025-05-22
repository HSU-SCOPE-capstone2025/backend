package com.scope.backend.scope_api.service;


import com.scope.backend.scope_api.domain.frontend.*;
import com.scope.backend.scope_api.dto.detail.*;
import com.scope.backend.scope_api.dto.frontend.*;
import com.scope.backend.scope_api.repository.frontend.*;
import com.scope.backend.scope_api.service.instagram.InfluencerAnalysisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InfluencerAnalysisServiceImpl implements InfluencerAnalysisService {

    private final InfluencerRepository influencerRepository;
    private final InstagramRepository instagramRepository;
    private final InstagramCommentRepository instagramCommentRepository;
    private final YoutubeCommentRepository youtubeCommentRepository;
    private final TiktokCommentRepository tiktokCommentRepository;
    private final TotalFollowerRepository totalFollowerRepository;

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
    public InfluencerAnalysisResponse getAnalysisByUserId(String userId) {
        Instagram firstInsta = instagramRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 인플루언서를 찾을 수 없습니다."));
        Influencer influencer = firstInsta.getInfluencer();
        Long influencerNum = influencer.getInfluencerNum();

        Pageable limit30 = PageRequest.of(0, 30);

// 📌 1️⃣ Instagram
        List<Map<String, Object>> instaRaw = instagramCommentRepository
                .findRecent30FssGroupedByDate(influencerNum, limit30);
        Collections.reverse(instaRaw); // 최신 → 과거 → 다시 과거 → 최신
        List<DateFssDto> instaFssList = getFssList(instaRaw);

// 📌 2️⃣ YouTube
        List<Map<String, Object>> youRaw = youtubeCommentRepository
                .findRecent30FssGroupedByDate(influencerNum, limit30);
        Collections.reverse(youRaw);
        List<DateFssDto> youFssList = getFssList(youRaw);

// 📌 3️⃣ TikTok
        List<Map<String, Object>> tikRaw = tiktokCommentRepository
                .findRecent30FssGroupedByDate(influencerNum, limit30);
        Collections.reverse(tikRaw);
        List<DateFssDto> tikFssList = getFssList(tikRaw);

// 📦 결과 조립
        PlatformScoresDto platformScores = new PlatformScoresDto();
        platformScores.setInstagram(instaFssList);
        platformScores.setYoutube(youFssList);
        platformScores.setTiktok(tikFssList);

        // 2️⃣ High FSS 비율 계산
        Long instaTotal = instagramCommentRepository.countAllByInfluencer(influencerNum);
        Long instaHigh = instagramCommentRepository.countHighFssByInfluencer(influencerNum);
        Long youTotal = youtubeCommentRepository.countAllByInfluencer(influencerNum);
        Long youHigh = youtubeCommentRepository.countHighFssByInfluencer(influencerNum);
        Long tikTotal = tiktokCommentRepository.countAllByInfluencer(influencerNum);
        Long tikHigh = tiktokCommentRepository.countHighFssByInfluencer(influencerNum);

        Long instaFollowers = totalFollowerRepository.findLatestFollowerCount(influencerNum, "Instagram").stream().findFirst().orElse(0L);
        Long youFollowers = totalFollowerRepository.findLatestFollowerCount(influencerNum, "YouTube").stream().findFirst().orElse(0L);
        Long tikFollowers = totalFollowerRepository.findLatestFollowerCount(influencerNum, "Tiktok").stream().findFirst().orElse(0L);

        // 3️⃣ 감정/성향/토픽 분석
        PlatformAnalysisDto instagram = getPlatformAnalysis(influencerNum, instagramCommentRepository);
        PlatformAnalysisDto youtube = getPlatformAnalysis(influencerNum, youtubeCommentRepository);
        PlatformAnalysisDto tiktok = getPlatformAnalysis(influencerNum, tiktokCommentRepository);

        // 4️⃣ DTO 조립
        return new InfluencerAnalysisResponse(
                influencer.getName(),
                platformScores,
                instaFollowers,
                safeDivide(instaHigh, instaTotal),
                youFollowers,
                safeDivide(youHigh, youTotal),
                tikFollowers,
                safeDivide(tikHigh, tikTotal),
                instagram,
                youtube,
                tiktok
        );
    }

    private List<DateFssDto> getFssList(List<Map<String, Object>> rawData) {
        return rawData.stream()
                .filter(entry -> entry.get("date") != null && entry.get("fss") != null)
                .map(entry -> {
                    String date = entry.get("date").toString();
                    Float fss = ((Double) entry.get("fss")).floatValue();
                    return new DateFssDto(date, fss);
                })
                .collect(Collectors.toList());
    }

    private double safeDivide(Long high, Long total) {
        return (total == 0) ? 0.0 : Math.round((high * 1.0 / total) * 1000.0) / 1000.0;
    }

    private PlatformAnalysisDto getPlatformAnalysis(Long influencerNum, Object repo) {
        List<NameValueDto> tendency = new ArrayList<>();
        List<NameValueDto> emotion = new ArrayList<>();
        List<TopicDto> topics = new ArrayList<>();

        // 성향
        List<Object[]> clusterCounts = getRawCounts(repo, "cluster", influencerNum);
        Long totalCluster = clusterCounts.stream().mapToLong(row -> (Long) row[1]).sum();
        for (Object[] row : clusterCounts) {
            String eng = row[0].toString();
            String kor = CLUSTER_KOR_MAP.getOrDefault(eng, eng);
            int value = (int) ((Long) row[1] * 100 / totalCluster);
            tendency.add(new NameValueDto(kor, value));
        }

        // 감정
        List<Object[]> emotionCounts = getRawCounts(repo, "emotion", influencerNum);
        Long totalEmotion = emotionCounts.stream().mapToLong(row -> (Long) row[1]).sum();
        for (Object[] row : emotionCounts) {
            String kor = row[0].toString();
            int value = (int) ((Long) row[1] * 100 / totalEmotion);
            emotion.add(new NameValueDto(kor, value));
        }

        // 토픽 + 댓글 샘플
        List<Object[]> topicCounts = getRawCounts(repo, "topic", influencerNum);
        Long totalTopic = topicCounts.stream().mapToLong(row -> (Long) row[1]).sum();
        for (Object[] row : topicCounts) {
            String topic = row[0].toString();
            int value = (int) ((Long) row[1] * 100 / totalTopic);
            List<String> comments = getRandomComments(repo, influencerNum, topic);
            topics.add(new TopicDto(topic, value, comments.isEmpty() ? null : comments));
        }

        return new PlatformAnalysisDto(tendency, emotion, topics);
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getRawCounts(Object repo, String field, Long influencerNum) {
        switch (field) {
            case "cluster" -> {
                if (repo instanceof InstagramCommentRepository ir) return ir.countClusterByInfluencer(influencerNum);
                if (repo instanceof YoutubeCommentRepository yr) return yr.countClusterByInfluencer(influencerNum);
                if (repo instanceof TiktokCommentRepository tr) return tr.countClusterByInfluencer(influencerNum);
            }
            case "emotion" -> {
                if (repo instanceof InstagramCommentRepository ir) return ir.countEmotionByInfluencer(influencerNum);
                if (repo instanceof YoutubeCommentRepository yr) return yr.countEmotionByInfluencer(influencerNum);
                if (repo instanceof TiktokCommentRepository tr) return tr.countEmotionByInfluencer(influencerNum);
            }
            case "topic" -> {
                if (repo instanceof InstagramCommentRepository ir) return ir.countTopicByInfluencer(influencerNum);
                if (repo instanceof YoutubeCommentRepository yr) return yr.countTopicByInfluencer(influencerNum);
                if (repo instanceof TiktokCommentRepository tr) return tr.countTopicByInfluencer(influencerNum);
            }
        }
        return Collections.emptyList();
    }

    private List<String> getRandomComments(Object repo, Long influencerNum, String topic) {
        if (repo instanceof InstagramCommentRepository ir)
            return ir.findRandomCommentsByTopic(influencerNum, topic, PageRequest.of(0, 5));
        if (repo instanceof YoutubeCommentRepository yr)
            return yr.findRandomCommentsByTopic(influencerNum, topic, PageRequest.of(0, 5));
        if (repo instanceof TiktokCommentRepository tr)
            return tr.findRandomCommentsByTopic(influencerNum, topic, PageRequest.of(0, 5));
        return List.of();
    }
}
