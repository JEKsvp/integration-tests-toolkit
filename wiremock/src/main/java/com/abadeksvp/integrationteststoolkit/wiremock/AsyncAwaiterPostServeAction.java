package com.abadeksvp.integrationteststoolkit.wiremock;

import java.util.List;

import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.extension.PostServeActionDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

/**
 * PostServeAction to countdown {@link WiremockAsyncAwaiter} after WireMock response been sent<br>
 * Do not forget to register it into test context:
 * <code>
 * <pre>
 *     &#64;Bean
 *     WireMockConfigurationCustomizer optionsCustomizer() {
 *         return options -> options.extensions(new AsyncAwaiterPostServeAction());
 *     }
 * </pre>
 * </code>
 *
 * @see WiremockTestConfiguration
 * @see WiremockAsyncAwaiter
 */
public class AsyncAwaiterPostServeAction extends PostServeAction {

    private static final String POST_SERVE_ACTION_NAME = AsyncAwaiterPostServeAction.class.getSimpleName();
    private static final String PARAM_NAME = "awaiter_latch";

    @Override
    public void doAction(ServeEvent serveEvent, Admin admin, Parameters parameters) {
        WiremockAsyncAwaiter awaiter = (WiremockAsyncAwaiter) parameters.get(PARAM_NAME);
        RequestInfo requestInfo = RequestInfo.builder()
                .url(serveEvent.getRequest().getUrl())
                .body(serveEvent.getRequest().getBodyAsString())
                .build();
        awaiter.countDown(requestInfo);
    }

    @Override
    public String getName() {
        return POST_SERVE_ACTION_NAME;
    }

    public static List<PostServeActionDefinition> postServeAwaiter(WiremockAsyncAwaiter awaiter) {
        return List.of(new PostServeActionDefinition(
                POST_SERVE_ACTION_NAME,
                Parameters.one(PARAM_NAME, awaiter)));
    }

}
