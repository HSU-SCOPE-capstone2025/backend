package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Instagram;
import com.scope.backend.scope_api.domain.frontend.Tiktok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TiktokRepository extends JpaRepository<Tiktok, String> {

    Optional<Tiktok> findFirstByInfluencer_InfluencerNum(Long influencerNum);
    List<Tiktok> findAllByInfluencer_InfluencerNum(Long influencerNum);
    Optional<Tiktok> findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(Long influencerNum);

    @Query("""
    SELECT NEW map(t.uploadDate AS date, t.followerNum AS followers, t.viewCount AS views, t.likeCount AS likes, t.commentCount AS comments)
    FROM Tiktok t
    WHERE t.influencer.influencerNum = :influencerNum
    ORDER BY t.uploadDate DESC
""")
    List<Map<String, Object>> findRecent21DailyStats(@Param("influencerNum") Long influencerNum, Pageable pageable);



    @Query("""
        SELECT c.fss 
        FROM Tiktok t 
        JOIN TiktokComment c ON t.videoUrl = c.tiktok.videoUrl 
        WHERE t.influencer.influencerNum = :influencerNum 
        ORDER BY t.uploadDate DESC
    """)
    List<Float> findFSSListByInfluencer(Long influencerNum);


    @Query("""
        SELECT t.likeCount 
        FROM Tiktok t 
        WHERE t.influencer.influencerNum = :influencerNum 
        ORDER BY t.uploadDate DESC
    """)
    List<Float> findLikeListByInfluencer(Long influencerNum);

    @Query("""
        SELECT t.commentCount
        FROM Tiktok t
        WHERE t.influencer.influencerNum = :influencerNum
    """)
    List<Float> findCommentCountListByInfluencer(Long influencerNum);


    @Query("""
        SELECT t.viewCount 
        FROM Tiktok t 
        WHERE t.influencer.influencerNum = :influencerNum 
        ORDER BY t.uploadDate DESC
    """)
    List<Float> findViewListByInfluencer(Long influencerNum);
}
