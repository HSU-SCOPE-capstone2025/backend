package com.scope.backend.scope_api.dto.detailsns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluencerSnsSummaryDto {
    private String name;
    private String instaId;
    private String youId;
    private String tikId;

    private String tags;        // "유머 / 예능, 감성 / 힐링"
    private String categories;  // "패션, 뷰티"

    private SnsDto sns;
}
