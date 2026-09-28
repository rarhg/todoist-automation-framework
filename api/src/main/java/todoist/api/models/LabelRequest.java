package todoist.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequest {

    private String name;
    private String color;
    private Integer order;

    @JsonProperty("is_favorite")
    private Boolean isFavorite;
}
