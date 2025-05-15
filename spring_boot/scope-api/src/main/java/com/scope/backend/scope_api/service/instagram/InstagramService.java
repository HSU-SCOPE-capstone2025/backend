//package com.scope.backend.scope_api.service.instagram;
//
//import com.scope.backend.scope_api.domain.instagram.InstagramFollower;
//import com.scope.backend.scope_api.domain.instagram.InstagramPost;
//import com.scope.backend.scope_api.dto.crawler.InfluencerFollowerDto;
//import com.scope.backend.scope_api.repository.instagram.InstagramFollowerRepository;
//import com.scope.backend.scope_api.repository.instagram.InstagramPostRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class InstagramService {
//
//    private final InstagramFollowerRepository repository;
//    private final InstagramPostRepository instagramPostRepository;
//
//    public void saveFollower(InfluencerFollowerDto dto) {
//        InstagramFollower follower = new InstagramFollower();
//        follower.setInstaName(dto.getInfluencer());
//        follower.setFollowerNum(dto.getFollowerNumber());
//        follower.setCreatedAt(dto.getAtTime().atStartOfDay());
//
//        repository.save(follower);
//    }
//
//    public InstagramPost savePost(InstagramPost instagramPost) {
//        instagramPost.setCreatedAt(LocalDate.now());
//        instagramPostRepository.save(instagramPost);
//        System.out.println("📌 Instagram Post 저장 완료: " + instagramPost);
//        return instagramPost;
//    }
//}
