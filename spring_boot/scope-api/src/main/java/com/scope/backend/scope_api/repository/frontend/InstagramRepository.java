package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Instagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstagramRepository extends JpaRepository<Instagram, Long> {

    @Query("SELECT i FROM Instagram i WHERE i.influencer.id = :influencerId ORDER BY i.postDate DESC")
    List<Instagram> findRecentPosts(@Param("influencerId") Long influencerId);
}
