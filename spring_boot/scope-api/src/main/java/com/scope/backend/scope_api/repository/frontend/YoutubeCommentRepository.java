package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.YoutubeComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeCommentRepository extends JpaRepository<YoutubeComment, Long> {

    @Query("""
        SELECT yc.cluster 
        FROM YoutubeComment yc 
        WHERE yc.youtube.videoUrl IN :videoUrls 
        GROUP BY yc.cluster 
        ORDER BY COUNT(yc.cluster) DESC
    """)
    List<String> findTopClusterByVideoUrls(@Param("videoUrls") List<String> videoUrls, Pageable pageable);

    @Query("""
        SELECT yc 
        FROM YoutubeComment yc 
        WHERE yc.youtube.videoUrl IN :videoUrls
    """)
    List<YoutubeComment> findByVideoUrlIn(@Param("videoUrls") List<String> videoUrls);

    @Query("""
        SELECT yc.fss 
        FROM YoutubeComment yc 
        WHERE yc.youtube.videoUrl IN :videoUrls 
        AND yc.fss IS NOT NULL
    """)
    List<Float> findFssByVideoUrls(@Param("videoUrls") List<String> videoUrls);
}
