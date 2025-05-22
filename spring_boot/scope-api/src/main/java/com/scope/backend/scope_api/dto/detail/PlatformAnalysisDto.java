package com.scope.backend.scope_api.dto.detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAnalysisDto {
    private List<NameValueDto> tendency;
    private List<NameValueDto> emotion;
    private List<TopicDto> topic;
}
