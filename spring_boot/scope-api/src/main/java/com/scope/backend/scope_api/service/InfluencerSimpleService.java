package com.scope.backend.scope_api.service;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import com.scope.backend.scope_api.domain.frontend.Instagram;
import com.scope.backend.scope_api.dto.frontend.InfluencerSimpleDto;
import com.scope.backend.scope_api.repository.frontend.InstagramRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InfluencerSimpleService {

    private final InstagramRepository instagramRepository;

    public InfluencerSimpleDto getByInstagramUserId(String userId) {
        Instagram instagram = instagramRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Instagram 계정을 찾을 수 없습니다."));

        Influencer influencer = instagram.getInfluencer();

        return InfluencerSimpleDto.builder()
                .id(userId)
                .name(influencer.getName())
                .category(influencer.getCategories()) // 문자열 그대로
                .tags(influencer.getTags())           // 문자열 그대로
                .build();
    }

    private List<String> splitComma(String input) {
        return input == null || input.isBlank()
                ? List.of()
                : Arrays.stream(input.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
