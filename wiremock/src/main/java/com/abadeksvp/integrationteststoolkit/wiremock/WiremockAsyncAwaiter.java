package com.abadeksvp.integrationteststoolkit.wiremock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.github.tomakehurst.wiremock.client.VerificationException;

import lombok.Getter;

/**
 * Synchronizer for verification of asynchronous calls to wiremock, works in pair with {@link AsyncAwaiterPostServeAction}<br>
 * Usage example:
 * <code>
 *     <pre>
 *    WiremockAsyncAwaiter awaiter = new WiremockAsyncAwaiter(1);
 *    WireMock.stubFor(
 *        WireMock.post(WireMock.urlEqualTo("/scope/notifications/v1/cards/approved/transaction"))
 *                .willReturn(emptyResponse))
 *        .setPostServeActions(AsyncAwaiterPostServeAction.postServeAwaiter(awaiter));
 *    // ... here is a code triggering request to wiremock
 *    awaiter.await(60000, TimeUnit.MILLISECONDS); // awaiter will wait till event processed, or throw
 *    // here you can verify requests received by WireMock
 *    WireMockUtils.verifyRequest(...
 *      </pre>
 * </code>
 * @see AsyncAwaiterPostServeAction
 */
public class WiremockAsyncAwaiter {

    @Getter
    private final CountDownLatch latch;
    private final int initCount;

    private final List<RequestInfo> acceptedRequests = new ArrayList<>();

    /**
     * @param count how many requests must be called before unlock awaited threads
     */
    public WiremockAsyncAwaiter(int count) {
        this.initCount = count;
        this.latch = new CountDownLatch(count);
    }

    /**
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @throws InterruptedException  if the current thread is interrupted while waiting
     * @throws VerificationException if expected requests count was not reached during timeout
     */
    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        boolean wasUnlocked = this.latch.await(timeout, unit);
        if (!wasUnlocked) {
            throw new VerificationException(
                    String.format("Not all async requests were called." +
                                    " Expected calls: %s, actual calls: %s, accepted requests:\n %s",
                            initCount, initCount - latch.getCount(), buildAcceptedRequestsLog()));
        }
    }

    private String buildAcceptedRequestsLog() {
        return this.acceptedRequests.stream()
                .map(request -> String.format("URL: %s,\n Body: %s\n",
                        request.getUrl(), request.getBody()))
                .reduce((r1, r2) -> r1 + "----------------\n" + r2)
                .orElse("NONE");
    }

    /**
     * decrement count of expected calls left
     */
    void countDown(RequestInfo request) {
        this.acceptedRequests.add(request);
        this.latch.countDown();
    }

}
