package todoist.api.specs;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.Set;

public class RetryOnServerErrorFilter implements Filter {

    private static final Set<Integer> RETRYABLE = Set.of(502, 503, 504);

    private final int maxAttempts;
    private final long delayMs;

    public RetryOnServerErrorFilter(int maxAttempts, long delayMs) {
        this.maxAttempts = maxAttempts;
        this.delayMs = delayMs;
    }

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        Response response = ctx.next(req, res);
        for (int attempt = 2; attempt <= maxAttempts && RETRYABLE.contains(response.statusCode()); attempt++) {
            try {
                Thread.sleep(delayMs * (attempt - 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            response = ctx.next(req, res);
        }
        return response;
    }
}