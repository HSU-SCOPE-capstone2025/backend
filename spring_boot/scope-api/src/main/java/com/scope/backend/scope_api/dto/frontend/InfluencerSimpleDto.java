package com.scope.backend.scope_api.dto.frontend;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfluencerSimpleDto {
    private String id;        // Instagram user_id
    private String name;      // Influencer name
    private String category;  // 그냥 문자열로 (예: "패션, 뷰티")
    private String tags;      // 그냥 문자열로 (예: "친근함")
}
