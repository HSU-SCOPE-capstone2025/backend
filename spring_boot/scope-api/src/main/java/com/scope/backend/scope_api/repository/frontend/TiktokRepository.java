package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Tiktok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TiktokRepository extends JpaRepository<Tiktok, String> {

    // ✅ 최근 7개의 비디오의 FSS 리스트로 가져오기
    @Query("""
        SELECT c.fss 
        FROM Tiktok t 
        JOIN TiktokComment c ON t.videoUrl = c.tiktok.videoUrl 
        WHERE t.influencer.influencerNum = :influencerNum 
        ORDER BY t.uploadDate DESC
    """)
    List<Float> findFSSListByInfluencer(Long influencerNum, Pageable pageable);

    // ✅ 최근 7개의 비디오의 좋아요 리스트로 가져오기
    @Query("""
        SELECT t.likeCount 
        FROM Tiktok t 
        WHERE t.influencer.influencerNum = :influencerNum 
        ORDER BY t.uploadDate DESC
    """)
    List<Float> findLikeListByInfluencer(Long influencerNum, Pageable pageable);

    // ✅ 최근 7개의 비디오의 조회수 리스트로 가져오기
    @Query("""
        SELECT t.viewCount 
        FROM Tiktok t 
        WHERE t.influencer.influencerNum = :influencerNum 
        ORDER BY t.uploadDate DESC
    """)
    List<Float> findViewListByInfluencer(Long influencerNum, Pageable pageable);
}
