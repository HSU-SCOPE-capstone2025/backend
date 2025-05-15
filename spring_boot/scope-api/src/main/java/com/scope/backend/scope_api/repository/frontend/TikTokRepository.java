package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TikTok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TikTokRepository extends JpaRepository<TikTok, Long> {

    @Query("SELECT t FROM TikTok t WHERE t.influencer.id = :influencerId ORDER BY t.uploadDate DESC")
    List<TikTok> findRecentVideos(@Param("influencerId") Long influencerId);
}
