package todoist.mobile.tests.functional.data;

import java.time.LocalDate;
import java.util.function.Supplier;

public record NlpCase(
        String inputPhrase,
        String taskCleanName,
        Supplier<LocalDate> expectedDateCalc
) {
    public LocalDate getExpectedDate() {
        return expectedDateCalc.get();
    }

    @Override
    public String toString() {
        return inputPhrase;
    }
}
