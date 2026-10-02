package todoist.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DueResponse {

    private String date;
    private String string;
    private String timezone;

    @JsonProperty("is_recurring")
    private Boolean isRecurring;

    public LocalDate toLocalDate() {
        return date == null ? null : LocalDate.parse(date.substring(0, 10));
    }
}