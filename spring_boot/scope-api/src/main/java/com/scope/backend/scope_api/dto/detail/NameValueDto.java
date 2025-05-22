package com.scope.backend.scope_api.dto.detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameValueDto {
    private String name;   // 한글 이름
    private int value;     // 퍼센트 값 (합쳐서 100)
}
