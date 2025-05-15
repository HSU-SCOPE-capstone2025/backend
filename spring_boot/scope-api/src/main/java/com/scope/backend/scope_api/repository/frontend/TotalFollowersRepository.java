package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.TotalFollowers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TotalFollowersRepository extends JpaRepository<TotalFollowers, Long> {

    // 특정 인플루언서의 플랫폼별 팔로워 수 찾기
    List<TotalFollowers> findByInfluencer(Influencer influencer);

    // 특정 인플루언서의 Instagram 팔로워 수 찾기
    @Query("SELECT tf FROM TotalFollowers tf WHERE tf.platform = 'instagram' AND tf.influencer = :influencer")
    Optional<TotalFollowers> findInstagramFollowers(@Param("influencer") Influencer influencer);

    // 특정 인플루언서의 TikTok 팔로워 수 찾기
    @Query("SELECT tf FROM TotalFollowers tf WHERE tf.platform = 'tiktok' AND tf.influencer = :influencer")
    Optional<TotalFollowers> findTiktokFollowers(@Param("influencer") Influencer influencer);

    // 특정 인플루언서의 YouTube 팔로워 수 찾기
    @Query("SELECT tf FROM TotalFollowers tf WHERE tf.platform = 'youtube' AND tf.influencer = :influencer")
    Optional<TotalFollowers> findYoutubeFollowers(@Param("influencer") Influencer influencer);
}
