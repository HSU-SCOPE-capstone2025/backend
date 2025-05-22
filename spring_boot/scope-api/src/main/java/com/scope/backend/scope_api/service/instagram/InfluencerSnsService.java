package com.scope.backend.scope_api.service.instagram;

import com.scope.backend.scope_api.dto.detailsns.InfluencerSnsSummaryDto;

public interface InfluencerSnsService {
    InfluencerSnsSummaryDto getSnsSummary(String userId);
}
