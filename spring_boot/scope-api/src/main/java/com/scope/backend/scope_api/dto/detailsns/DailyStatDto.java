package com.scope.backend.scope_api.dto.detailsns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyStatDto {
    private String date;
    private Long followers;
    private Long likes;
    private Long comments;
    private Long views; // Instagram은 null 가능
}