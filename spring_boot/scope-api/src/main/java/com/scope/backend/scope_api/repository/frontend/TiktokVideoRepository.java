package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.TiktokVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiktokVideoRepository extends JpaRepository<TiktokVideo, Long> {

    // 특정 인플루언서의 최근 7개 틱톡 영상 조회
    @Query("SELECT tv FROM TiktokVideo tv WHERE tv.influencer = :influencer ORDER BY tv.uploadDate DESC")
    List<TiktokVideo> findTop7ByInfluencerOrderByUploadDateDesc(@Param("influencer") Influencer influencer);
}
