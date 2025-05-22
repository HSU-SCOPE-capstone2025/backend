package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TiktokComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TiktokCommentRepository extends JpaRepository<TiktokComment, Long> {

    @Query("""
        SELECT tc.cluster 
        FROM TiktokComment tc 
        WHERE tc.tiktok.videoUrl IN :videoUrls 
        GROUP BY tc.cluster 
        ORDER BY COUNT(tc.cluster) DESC
    """)
    List<String> findTopClusterByVideoUrls(@Param("videoUrls") List<String> videoUrls, Pageable pageable);

    @Query("""
        SELECT tc 
        FROM TiktokComment tc 
        WHERE tc.tiktok.videoUrl IN :videoUrls
    """)
    List<TiktokComment> findByVideoUrlIn(@Param("videoUrls") List<String> videoUrls);

    @Query("""
        SELECT tc.fss 
        FROM TiktokComment tc 
        WHERE tc.tiktok.videoUrl IN :videoUrls 
        AND tc.fss IS NOT NULL
    """)
    List<Float> findFssByVideoUrls(@Param("videoUrls") List<String> videoUrls);

    @Query("""
    SELECT NEW map(tc.commentDate AS date, AVG(tc.fss) AS fss)
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum
    GROUP BY tc.commentDate
    ORDER BY tc.commentDate DESC
""")
    List<Map<String, Object>> findRecent30FssGroupedByDate(
            @Param("influencerNum") Long influencerNum,
            Pageable pageable);

    @Query("""
    SELECT COUNT(tc)
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum
""")
    Long countAllByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT COUNT(tc)
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum
    AND tc.fss >= 40
""")
    Long countHighFssByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT tc.cluster, COUNT(tc)
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum
    GROUP BY tc.cluster
""")
    List<Object[]> countClusterByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT tc.emotion, COUNT(tc)
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum
    GROUP BY tc.emotion
""")
    List<Object[]> countEmotionByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT tc.topic, COUNT(tc)
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum
    GROUP BY tc.topic
""")
    List<Object[]> countTopicByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT tc.comment
    FROM TiktokComment tc
    WHERE tc.tiktok.influencer.influencerNum = :influencerNum AND tc.topic = :topic
    ORDER BY function('RAND')
""")
    List<String> findRandomCommentsByTopic(@Param("influencerNum") Long influencerNum, @Param("topic") String topic, Pageable pageable);


}
