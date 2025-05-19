package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TotalFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TotalFollowerRepository extends JpaRepository<TotalFollower, Long> {

    Optional<TotalFollower> findTopByInfluencer_InfluencerNumAndPlatformOrderByDateDesc(Long influencerNum, String platform);

    @Query("""
        SELECT t.subscriberCount 
        FROM TotalFollower t 
        WHERE t.influencer.influencerNum = :influencerNum 
        AND t.platform = :platform 
        ORDER BY t.date DESC 
    """)
    List<Long> findLatestFollowerCount(Long influencerNum, String platform);
}
