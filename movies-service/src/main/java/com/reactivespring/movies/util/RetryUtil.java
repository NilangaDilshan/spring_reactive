package com.reactivespring.movies.util;

import com.reactivespring.movies.exception.MoviesInfoServerException;
import com.reactivespring.movies.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

public class RetryUtil {
    public static Retry retrySpec() {
        return Retry.fixedDelay(3, java.time.Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof MoviesInfoServerException ||
                        throwable instanceof ReviewsServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                   /* log.error("Retry exhausted: {}", retrySignal.totalRetries());
                    return new RuntimeException("Retry exhausted");*/
                    return Exceptions.propagate(retrySignal.failure());
                });
    }
}
