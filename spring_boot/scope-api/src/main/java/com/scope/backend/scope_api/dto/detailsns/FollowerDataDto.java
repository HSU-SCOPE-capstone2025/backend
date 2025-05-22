package com.scope.backend.scope_api.dto.detailsns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // <- 이게 없으면 객체를 JSON으로 만들 때 값이 빠짐
@AllArgsConstructor
@NoArgsConstructor

public class FollowerDataDto {
    private String date;
    private Long followers;
}