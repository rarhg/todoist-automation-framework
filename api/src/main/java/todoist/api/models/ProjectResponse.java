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
public class ProjectResponse {

    private String id;
    private String name;
    private String color;

    @JsonProperty("is_favorite")
    private Boolean isFavorite;

    @JsonProperty("inbox_project")
    private Boolean isInboxProject;

    private String url;
}