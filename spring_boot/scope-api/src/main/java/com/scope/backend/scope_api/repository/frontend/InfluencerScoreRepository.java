package com.scope.backend.scope_api.repository.frontend;


import com.scope.backend.scope_api.domain.frontend.InfluencersScore;
import com.scope.backend.scope_api.dto.frontend.InfluencerSearchDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfluencerScoreRepository extends JpaRepository<InfluencersScore, Long> {

    List<InfluencersScore> findAll();

    // ✅ DTO 매핑용 JPQL (컬럼만 뽑기)
    @Query("SELECT new com.scope.backend.scope_api.dto.frontend.InfluencerSearchDto(i.name, i.followers, i.averageViews, i.averageComments, i.averageLikes) FROM InfluencersScore i ORDER BY i.ffs DESC")
    List<InfluencerSearchDto> findAllProjectedByOrderByFfsDesc();

}
