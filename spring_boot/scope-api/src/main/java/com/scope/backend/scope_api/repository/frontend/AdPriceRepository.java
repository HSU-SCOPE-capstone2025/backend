package com.scope.backend.scope_api.repository.frontend;


import com.scope.backend.scope_api.domain.frontend.AdPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdPriceRepository extends JpaRepository<AdPrice, Long> {
    AdPrice findByInfluencerNum(Long influencerNum);
}
