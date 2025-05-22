package com.scope.backend.scope_api.dto.detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private String name;
    private int value;
    private List<String> comments;  // 최대 5개 or null
}
