package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.frontend.InfluencerRankingResponse;
import com.scope.backend.scope_api.service.instagram.InfluencerRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerRankingController {

    @Autowired
    private InfluencerRankingService influencerRankingService;

    @GetMapping("/ranking")
    public List<InfluencerRankingResponse> getTopInfluencers() {
        return influencerRankingService.getTopInfluencers();
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search() {
        // 1️⃣ 데이터 가져오기
        List<InfluencerRankingResponse> influencers = influencerRankingService.getTopInfluencers();

        // 2️⃣ JSON 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("influencers", influencers);
        response.put("total_influencer_num", influencers.size());


        // 3️⃣ ResponseEntity로 반환 (200 OK)
        return ResponseEntity.ok(response);
    }
}
