package com.scope.backend.scope_api.service.instagram;

import com.scope.backend.scope_api.domain.instagram.InstagramFollower;
import com.scope.backend.scope_api.dto.crawler.InfluencerFollowerDto;
import com.scope.backend.scope_api.repository.instagram.InstagramFollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InstagramService {

    private final InstagramFollowerRepository repository;

    public void saveFollower(InfluencerFollowerDto dto) {
        InstagramFollower follower = new InstagramFollower();
        follower.setInstaName(dto.getInfluencer());
        follower.setFollowerNum(dto.getFollowerNumber());
        follower.setCreatedAt(dto.getAtTime().atStartOfDay());

        repository.save(follower);
    }
}
