package com.scope.backend.scope_api.service.instagram;

import com.scope.backend.scope_api.dto.detail.InfluencerAnalysisResponse;

public interface InfluencerAnalysisService {
    InfluencerAnalysisResponse getAnalysisByUserId(String userId);
}
