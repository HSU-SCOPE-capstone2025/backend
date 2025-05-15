package com.scope.backend.scope_api.repository.frontend;

import com.scope.backend.scope_api.domain.frontend.Influencer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

    // 이름으로 인플루언서 검색
    Optional<Influencer> findByName(String name);

    // 30명의 인플루언서 데이터를 가져오는 메서드
    @Query("SELECT i FROM Influencer i ORDER BY i.name ASC")
    Page<Influencer> findTop30Influencers(Pageable pageable);
}
