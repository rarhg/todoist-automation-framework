package todoist.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentResponse {

    private String id;

    @JsonProperty("item_id")
    private String taskId;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("posted_uid")
    private String postedUid;

    private String content;

    @JsonProperty("posted_at")
    private String postedAt;
}