package com.scope.backend.scope_api.service.instagram;


import com.scope.backend.scope_api.dto.frontend.InfluencerRankingResponse;

import java.util.List;

public interface InfluencerRankingService {

    // 상위 30명 가져오기
    List<InfluencerRankingResponse> getTopInfluencers();

    // 특정 인플루언서의 랭킹 정보를 가져오는 메서드
    // nfluencerRankingResponse getInfluencerRanking(Long influencerNum);
}
