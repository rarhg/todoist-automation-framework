package todoist.api.specs;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import javax.net.ssl.SSLException;
import java.net.SocketException;
import java.net.UnknownHostException;
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
        Response response = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                response = ctx.next(req, res);
                if (!RETRYABLE.contains(response.statusCode())) {
                    return response;
                }
            } catch (Exception e) {
                if (!isTransientNetworkError(e) || attempt == maxAttempts) {
                    throw e instanceof RuntimeException re ? re : new RuntimeException(e);
                }
            }
            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(delayMs * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return response;
    }

    private static boolean isTransientNetworkError(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SSLException || t instanceof SocketException || t instanceof UnknownHostException) {
                return true;
            }
        }
        return false;
    }
}