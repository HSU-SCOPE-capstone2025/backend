package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Instagram;
import com.scope.backend.scope_api.domain.frontend.InstagramComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstagramRepository extends JpaRepository<Instagram, String> {
    Optional<Instagram> findFirstByInfluencer_InfluencerNum(Long influencerNum);
    List<Instagram> findAllByInfluencer_InfluencerNum(Long influencerNum);
    Optional<Instagram> findFirstByInfluencer_InfluencerNumOrderByPostDateDesc(Long influencerNum);



    @Query("""
        SELECT c.fss 
        FROM Instagram i 
        JOIN InstagramComment c ON i.postUrl = c.instagram.postUrl 
        WHERE i.influencer.influencerNum = :influencerNum 
        ORDER BY i.postDate DESC
    """)
    List<Float> findFSSListByInfluencer(Long influencerNum);

    @Query("""
        SELECT i.commentCount
        FROM Instagram i
        WHERE i.influencer.influencerNum = :influencerNum
    """)
    List<Float> findCommentCountListByInfluencer(Long influencerNum);

    @Query("""
        SELECT i.likeCount 
        FROM Instagram i 
        WHERE i.influencer.influencerNum = :influencerNum 
        ORDER BY i.postDate DESC
    """)
    List<Float> findLikeListByInfluencer(Long influencerNum);
}
