package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Tiktok;
import com.scope.backend.scope_api.domain.frontend.Youtube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface YoutubeRepository extends JpaRepository<Youtube, String> {

    Optional<Youtube> findFirstByInfluencer_InfluencerNum(Long influencerNum);
    List<Youtube> findAllByInfluencer_InfluencerNum(Long influencerNum);
    Optional<Youtube> findFirstByInfluencer_InfluencerNumOrderByUploadDateDesc(Long influencerNum);


    @Query("""
        SELECT c.fss 
        FROM Youtube y 
        JOIN YoutubeComment c ON y.videoUrl = c.youtube.videoUrl 
        WHERE y.influencer.influencerNum = :influencerNum 
        ORDER BY y.uploadDate DESC
    """)
    List<Float> findFSSListByInfluencer(Long influencerNum);


    @Query("""
        SELECT y.likeCount 
        FROM Youtube y 
        WHERE y.influencer.influencerNum = :influencerNum 
        ORDER BY y.uploadDate DESC
    """)
    List<Float> findLikeListByInfluencer(Long influencerNum);

    @Query("""
        SELECT y.commentCount
        FROM Youtube y
        WHERE y.influencer.influencerNum = :influencerNum
    """)
    List<Float> findCommentCountListByInfluencer(Long influencerNum);


    @Query("""
        SELECT y.viewCount 
        FROM Youtube y 
        WHERE y.influencer.influencerNum = :influencerNum 
        ORDER BY y.uploadDate DESC
    """)
    List<Float> findViewListByInfluencer(Long influencerNum);

}
