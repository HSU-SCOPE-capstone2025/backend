package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.InstagramComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstagramCommentRepository extends JpaRepository<InstagramComment, Long> {

    @Query("""
        SELECT ic.cluster 
        FROM InstagramComment ic 
        WHERE ic.instagram.postUrl IN :postUrls 
        GROUP BY ic.cluster 
        ORDER BY COUNT(ic.cluster) DESC
    """)
    List<String> findTopClusterByPostUrls(@Param("postUrls") List<String> postUrls, Pageable pageable);

    @Query("""
        SELECT ic 
        FROM InstagramComment ic 
        WHERE ic.instagram.postUrl IN :postUrls
    """)
    List<InstagramComment> findByInstagram_PostUrlIn(@Param("postUrls") List<String> postUrls);

    @Query("""
        SELECT ic.fss 
        FROM InstagramComment ic 
        WHERE ic.instagram.postUrl IN :postUrls 
        AND ic.fss IS NOT NULL
    """)
    List<Float> findFssByPostUrls(@Param("postUrls") List<String> postUrls);
}
