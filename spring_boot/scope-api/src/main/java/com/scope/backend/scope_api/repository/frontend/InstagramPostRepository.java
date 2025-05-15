package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.InstagramPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstagramPostRepository extends JpaRepository<InstagramPost, Long> {

    // 특정 인플루언서의 최근 7개 포스트 조회
    @Query("SELECT ip FROM InstagramPost ip WHERE ip.influencer = :influencer ORDER BY ip.atTime DESC")
    List<InstagramPost> findTop7ByInfluencerOrderByAtTimeDesc(@Param("influencer") Influencer influencer);
}
