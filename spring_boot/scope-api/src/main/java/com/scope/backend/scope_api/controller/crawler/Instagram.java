//package com.scope.backend.scope_api.controller.crawler;
//
//import com.scope.backend.scope_api.domain.instagram.InstagramPost;
//import com.scope.backend.scope_api.dto.crawler.InfluencerFollowerDto;
//import com.scope.backend.scope_api.service.instagram.InstagramService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/instagram")
//@RequiredArgsConstructor
//public class Instagram {
//
//    private final InstagramService instagramService;
//
//    @PostMapping("/influencer")
//    public ResponseEntity<String> receiveFollower(@RequestBody InfluencerFollowerDto dto) {
//        System.out.println("받은 데이터: " + dto);
//        instagramService.saveFollower(dto);
//        return ResponseEntity.ok("📦 저장 완료!");
//    }
//
//    @PostMapping("/posts")
//    public ResponseEntity<Map<String, Long>> receivePost(@RequestBody InstagramPost instagramPost){
//
//        System.out.println("받은 데이터: " + instagramPost.toString());
//
//        InstagramPost savedPost = instagramService.savePost(instagramPost);
//        System.out.println("저장된 데이터: " + savedPost.toString());
//        // JSON 형태로 반환
//        Map<String, Long> response = new HashMap<>();
//        response.put("postId", savedPost.getId());
//
//        return ResponseEntity.ok(response);
//    }
//
//
//}
