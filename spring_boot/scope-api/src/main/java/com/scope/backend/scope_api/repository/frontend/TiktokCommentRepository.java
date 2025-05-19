package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.TiktokComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiktokCommentRepository extends JpaRepository<TiktokComment, Long> {

    @Query("""
        SELECT tc.cluster 
        FROM TiktokComment tc 
        WHERE tc.tiktok.videoUrl IN :videoUrls 
        GROUP BY tc.cluster 
        ORDER BY COUNT(tc.cluster) DESC
    """)
    List<String> findTopClusterByVideoUrls(@Param("videoUrls") List<String> videoUrls, Pageable pageable);

    @Query("""
        SELECT tc 
        FROM TiktokComment tc 
        WHERE tc.tiktok.videoUrl IN :videoUrls
    """)
    List<TiktokComment> findByVideoUrlIn(@Param("videoUrls") List<String> videoUrls);

    @Query("""
        SELECT tc.fss 
        FROM TiktokComment tc 
        WHERE tc.tiktok.videoUrl IN :videoUrls 
        AND tc.fss IS NOT NULL
    """)
    List<Float> findFssByVideoUrls(@Param("videoUrls") List<String> videoUrls);
}
