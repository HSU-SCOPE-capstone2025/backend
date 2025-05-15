package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.YouTube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeRepository extends JpaRepository<YouTube, Long> {

    @Query("SELECT y FROM YouTube y WHERE y.influencer.id = :influencerId ORDER BY y.uploadDate DESC")
    List<YouTube> findRecentVideos(@Param("influencerId") Long influencerId);
}
