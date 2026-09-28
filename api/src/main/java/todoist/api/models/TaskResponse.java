package todoist.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponse {

    private String id;
    private String content;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    private Integer priority;
    private List<String> labels;
    private String url;

    @JsonProperty("created_at")
    private String createdAt;
}
