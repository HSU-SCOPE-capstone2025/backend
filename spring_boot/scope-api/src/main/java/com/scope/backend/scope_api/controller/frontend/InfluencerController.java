package com.scope.backend.scope_api.controller.frontend;

import com.scope.backend.scope_api.dto.frontend.InfluencerDTO;
import com.scope.backend.scope_api.service.InfluencerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
@RequiredArgsConstructor
public class InfluencerController {

    private final InfluencerService influencerService;

    // ✅ 30명의 인플루언서 랭킹 데이터 조회
    @GetMapping("/ranking")
    public ResponseEntity<List<InfluencerDTO>> getInfluencerRanking() {
        List<InfluencerDTO> influencers = influencerService.getTop30Influencers();
        return ResponseEntity.ok(influencers);
    }
}
