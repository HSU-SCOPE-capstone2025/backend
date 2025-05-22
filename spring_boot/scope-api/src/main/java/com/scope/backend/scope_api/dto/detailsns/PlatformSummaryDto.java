package com.scope.backend.scope_api.dto.detailsns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformSummaryDto {
    private Long followers;
    private Long averageLikes;
    private Long averageComments;
    private Long averageViews; // Instagram은 null 가능
    private Float fss;
    private String clusters;
    private String url;


    private String adPrice; // 또는 youAd, tikAd (플랫폼별 맞게 설정)
    private List<DailyStatDto> dailyStats;

    // YouTube만
    private List<YoutubeThumbnailDto> youtubeThumbnails;
}
