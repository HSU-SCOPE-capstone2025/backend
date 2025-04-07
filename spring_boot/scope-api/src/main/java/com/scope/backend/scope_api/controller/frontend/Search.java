package com.scope.backend.scope_api.controller.frontend;


import com.scope.backend.scope_api.dto.frontend.InfluencerSearchDto;
import com.scope.backend.scope_api.repository.frontend.InfluencerScoreRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Search {

    private final InfluencerScoreRepository repository;
    public Search(InfluencerScoreRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/influencers/search")
    public List<InfluencerSearchDto> influencerSearch() {
        return repository.findAllProjectedByOrderByFfsDesc();
    }
}
