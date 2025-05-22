package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.YoutubeComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface YoutubeCommentRepository extends JpaRepository<YoutubeComment, Long> {

    @Query("""
        SELECT yc.cluster 
        FROM YoutubeComment yc 
        WHERE yc.youtube.videoUrl IN :videoUrls 
        GROUP BY yc.cluster 
        ORDER BY COUNT(yc.cluster) DESC
    """)
    List<String> findTopClusterByVideoUrls(@Param("videoUrls") List<String> videoUrls, Pageable pageable);

    @Query("""
        SELECT yc 
        FROM YoutubeComment yc 
        WHERE yc.youtube.videoUrl IN :videoUrls
    """)
    List<YoutubeComment> findByVideoUrlIn(@Param("videoUrls") List<String> videoUrls);

    @Query("""
        SELECT yc.fss 
        FROM YoutubeComment yc 
        WHERE yc.youtube.videoUrl IN :videoUrls 
        AND yc.fss IS NOT NULL
    """)
    List<Float> findFssByVideoUrls(@Param("videoUrls") List<String> videoUrls);

    @Query("""
    SELECT NEW map(yc.commentDate AS date, AVG(yc.fss) AS fss)
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum
    GROUP BY yc.commentDate
    ORDER BY yc.commentDate DESC
""")
    List<Map<String, Object>> findRecent30FssGroupedByDate(
            @Param("influencerNum") Long influencerNum,
            Pageable pageable);

    @Query("""
    SELECT COUNT(yc)
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum
""")
    Long countAllByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT COUNT(yc)
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum
    AND yc.fss >= 40
""")
    Long countHighFssByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT yc.cluster, COUNT(yc)
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum
    GROUP BY yc.cluster
""")
    List<Object[]> countClusterByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT yc.emotion, COUNT(yc)
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum
    GROUP BY yc.emotion
""")
    List<Object[]> countEmotionByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT yc.topic, COUNT(yc)
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum
    GROUP BY yc.topic
""")
    List<Object[]> countTopicByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT yc.comment
    FROM YoutubeComment yc
    WHERE yc.youtube.influencer.influencerNum = :influencerNum AND yc.topic = :topic
    ORDER BY function('RAND')
""")
    List<String> findRandomCommentsByTopic(@Param("influencerNum") Long influencerNum, @Param("topic") String topic, Pageable pageable);


}
