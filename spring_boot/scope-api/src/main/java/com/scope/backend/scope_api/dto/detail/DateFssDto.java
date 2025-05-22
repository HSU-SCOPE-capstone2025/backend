package com.scope.backend.scope_api.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateFssDto {
    private String date;  // "yyyy-MM-dd"
    private Float fss;
}
