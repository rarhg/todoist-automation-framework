package todoist.api.models;

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
public class TaskRequest {

    private String content;
    private String description;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("due_string")
    private String dueString;

    private Integer priority;
    private List<String> labels;
}
