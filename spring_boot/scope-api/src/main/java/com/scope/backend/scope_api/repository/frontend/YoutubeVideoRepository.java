package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.YoutubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, Long> {

    // 특정 인플루언서의 최근 7개 유튜브 영상 조회
    @Query("SELECT yv FROM YoutubeVideo yv WHERE yv.influencer = :influencer ORDER BY yv.uploadDate DESC")
    List<YoutubeVideo> findTop7ByInfluencerOrderByUploadDateDesc(@Param("influencer") Influencer influencer);
}

