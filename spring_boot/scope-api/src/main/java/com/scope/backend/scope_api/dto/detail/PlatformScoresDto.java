package com.scope.backend.scope_api.dto.detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformScoresDto {
    private List<DateFssDto> instagram;
    private List<DateFssDto> youtube;
    private List<DateFssDto> tiktok;
}
