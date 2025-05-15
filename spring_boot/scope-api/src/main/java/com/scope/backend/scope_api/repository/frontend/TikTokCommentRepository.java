package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TikTokComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TikTokCommentRepository extends JpaRepository<TikTokComment, Long> {

    @Query("SELECT tc FROM TikTokComment tc WHERE tc.tiktok.id = :tiktokId ORDER BY tc.commentDate DESC")
    List<TikTokComment> findRecentComments(@Param("tiktokId") Long tiktokId);
}
