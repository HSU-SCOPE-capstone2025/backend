package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TotalFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalFollowerRepository extends JpaRepository<TotalFollower, Long> {

    // 플랫폼별 최신 구독자 수 조회
    @Query("""
        SELECT t.subscriberCount 
        FROM TotalFollower t 
        WHERE t.influencer.influencerNum = :influencerNum 
        AND t.platform = :platform 
        ORDER BY t.date DESC 
        LIMIT 1
    """)
    Long findLatestFollowerCount(Long influencerNum, String platform);
}