package com.scope.backend.scope_api.controller.crawler;

import com.scope.backend.scope_api.dto.crawler.InfluencerFollowerDto;
import com.scope.backend.scope_api.service.instagram.InstagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instagram")
@RequiredArgsConstructor
public class Instagram {

    private final InstagramService instagramService;

    @PostMapping("/influencer")
    public ResponseEntity<String> receiveFollower(@RequestBody InfluencerFollowerDto dto) {
        System.out.println("받은 데이터: " + dto);
        instagramService.saveFollower(dto);
        return ResponseEntity.ok("📦 저장 완료!");
    }


}
