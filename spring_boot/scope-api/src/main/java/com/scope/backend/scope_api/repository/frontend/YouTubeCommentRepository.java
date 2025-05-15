package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.YouTubeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeCommentRepository extends JpaRepository<YouTubeComment, Long> {

    @Query("SELECT yc FROM YouTubeComment yc WHERE yc.youtube.id = :youtubeId ORDER BY yc.commentDate DESC")
    List<YouTubeComment> findRecentComments(@Param("youtubeId") Long youtubeId);
}
