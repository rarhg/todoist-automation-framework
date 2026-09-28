package todoist.mobile.tests.functional.data;

import org.junit.jupiter.params.provider.Arguments;
import java.time.LocalDate;
import java.util.Random;
import java.util.stream.Stream;

public final class NlpDataProvider {

    private NlpDataProvider() {}

    public static Stream<Arguments> provideDynamicNlpCases() {
        Random random = new Random();

        int randomDays = random.nextInt(28) + 3;
        int randomMonths = random.nextInt(10) + 2;
        int randomHour = random.nextInt(11) + 1;

        String baseText = "Сдать диплом";

        return Stream.of(
                Arguments.of(new NlpCase(baseText + " сегодня в 14:00", baseText, LocalDate::now)),
                Arguments.of(new NlpCase(baseText + " завтра в 20", baseText, () -> LocalDate.now().plusDays(1))),

                Arguments.of(new NlpCase(
                        String.format("%s через %d дней в 15:00", baseText, randomDays),
                        baseText,
                        () -> LocalDate.now().plusDays(randomDays)
                )),
                Arguments.of(new NlpCase(
                        String.format("%s через %d месяца в 6 pm", baseText, randomMonths),
                        baseText,
                        () -> LocalDate.now().plusMonths(randomMonths)
                )),
                Arguments.of(new NlpCase(
                        String.format("%s послезавтра в %d pm", baseText, randomHour),
                        baseText,
                        () -> LocalDate.now().plusDays(2)
                ))
        );
    }
}