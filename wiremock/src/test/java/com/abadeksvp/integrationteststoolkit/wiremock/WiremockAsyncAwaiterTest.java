package com.abadeksvp.integrationteststoolkit.wiremock;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.abadeksvp.integrationteststoolkit.resource.ClasspathResourceReader;
import com.abadeksvp.integrationteststoolkit.resource.ResourceReader;
import com.github.tomakehurst.wiremock.client.VerificationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WiremockAsyncAwaiterTest {

    @Test
    public void properExceptionMessageFormatForJsonBodyTest() throws InterruptedException {
        WiremockAsyncAwaiter awaiter = new WiremockAsyncAwaiter(3);
        awaiter.countDown(new RequestInfo("http://localhost:8080/test/test", """
                {"key1": "value1","key2": "value2"}
                """));
        awaiter.countDown(new RequestInfo("http://localhost:8080/test/test", """
                {"key11": "value11","key22": "value22"}
                """));
        assertThatThrownBy(() -> awaiter.await(100, TimeUnit.MICROSECONDS))
                .isInstanceOf(VerificationException.class)
                .hasMessageContaining("""
                        Not all async requests were called.
                        Expected requests count: 3
                        Actual requests count: 2
                        Received requests:
                        URL: http://localhost:8080/test/test,
                        Body:
                        """);
        //todo add json body to exception message verification
    }

    @Test
    public void properExceptionMessageFormatNonJsonBodyTest() throws InterruptedException {
        WiremockAsyncAwaiter awaiter = new WiremockAsyncAwaiter(3);
        awaiter.countDown(new RequestInfo("http://localhost:8080/test/test", """
                12335346457657sdfgdsfg
                """));
        awaiter.countDown(new RequestInfo("http://localhost:8080/test/test", """
                <xml>
                    <key11>value11</key11>
                    <key22>value22</key22>
                </xml>
                """));
        assertThatThrownBy(() -> awaiter.await(100, TimeUnit.MICROSECONDS))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""
                        Not all async requests were called.
                        Expected requests count: 3
                        Actual requests count: 2
                        Received requests:
                        URL: http://localhost:8080/test/test,
                        Body:
                        12335346457657sdfgdsfg
                        ----------------
                        URL: http://localhost:8080/test/test,
                        Body:
                        <xml><key11>value11</key11><key22>value22</key22></xml>
                        """);
    }
}