package com.scope.backend.scope_api.dto.detailsns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data  // <- 이게 빠졌다면 반드시 추가해야 함
@AllArgsConstructor
@NoArgsConstructor
public class FollowerHistoryDto {
    private List<FollowerDataDto> instafollowers;
    private List<FollowerDataDto> tiktokfollowers;
    private List<FollowerDataDto> youtubefollowers;
}
