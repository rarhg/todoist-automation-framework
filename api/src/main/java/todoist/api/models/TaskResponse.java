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

    private Boolean checked;

    private Integer priority;
    private List<String> labels;
    private DueResponse due;

    @JsonProperty("added_at")
    private String addedAt;
}