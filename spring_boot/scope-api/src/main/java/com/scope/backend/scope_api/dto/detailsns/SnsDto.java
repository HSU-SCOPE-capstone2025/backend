package com.scope.backend.scope_api.dto.detailsns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnsDto {
    private PlatformSummaryDto instagram;
    private PlatformSummaryDto youtube;
    private PlatformSummaryDto tiktok;
}
