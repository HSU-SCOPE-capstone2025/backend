package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.InstagramComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InstagramCommentRepository extends JpaRepository<InstagramComment, Long> {

    @Query("""
        SELECT ic.cluster 
        FROM InstagramComment ic 
        WHERE ic.instagram.postUrl IN :postUrls 
        GROUP BY ic.cluster 
        ORDER BY COUNT(ic.cluster) DESC
    """)
    List<String> findTopClusterByPostUrls(@Param("postUrls") List<String> postUrls, Pageable pageable);

    @Query("""
        SELECT ic 
        FROM InstagramComment ic 
        WHERE ic.instagram.postUrl IN :postUrls
    """)
    List<InstagramComment> findByInstagram_PostUrlIn(@Param("postUrls") List<String> postUrls);

    @Query("""
        SELECT ic.fss 
        FROM InstagramComment ic 
        WHERE ic.instagram.postUrl IN :postUrls 
        AND ic.fss IS NOT NULL
    """)
    List<Float> findFssByPostUrls(@Param("postUrls") List<String> postUrls);

    @Query("""
    SELECT NEW map(ic.commentDate AS date, AVG(ic.fss) AS fss)
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum
    GROUP BY ic.commentDate
    ORDER BY ic.commentDate DESC
""")
    List<Map<String, Object>> findRecent30FssGroupedByDate(
            @Param("influencerNum") Long influencerNum,
            Pageable pageable);

    @Query("""
    SELECT COUNT(ic)
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum
""")
    Long countAllByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT COUNT(ic)
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum
    AND ic.fss >= 40
""")
    Long countHighFssByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT ic.cluster, COUNT(ic)
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum
    GROUP BY ic.cluster
""")
    List<Object[]> countClusterByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT ic.emotion, COUNT(ic)
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum
    GROUP BY ic.emotion
""")
    List<Object[]> countEmotionByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT ic.topic, COUNT(ic)
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum
    GROUP BY ic.topic
""")
    List<Object[]> countTopicByInfluencer(@Param("influencerNum") Long influencerNum);

    @Query("""
    SELECT ic.comment
    FROM InstagramComment ic
    WHERE ic.instagram.influencer.influencerNum = :influencerNum AND ic.topic = :topic
    ORDER BY function('RAND')
""")
    List<String> findRandomCommentsByTopic(@Param("influencerNum") Long influencerNum, @Param("topic") String topic, Pageable pageable);

}
