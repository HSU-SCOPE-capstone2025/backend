package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.InstagramComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstagramCommentRepository extends JpaRepository<InstagramComment, Long> {

    @Query("SELECT ic FROM InstagramComment ic WHERE ic.instagram.id = :instagramId ORDER BY ic.commentDate DESC")
    List<InstagramComment> findRecentComments(@Param("instagramId") Long instagramId);
}
