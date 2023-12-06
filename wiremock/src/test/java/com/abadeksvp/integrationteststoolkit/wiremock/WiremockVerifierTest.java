package com.abadeksvp.integrationteststoolkit.wiremock;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.client.RestTemplate;

import com.abadeksvp.integrationteststoolkit.JsonAssertUtils;
import com.abadeksvp.integrationteststoolkit.resource.ClasspathResourceReader;
import com.abadeksvp.integrationteststoolkit.resource.ResourceReader;
import com.abadeksvp.integrationteststoolkit.wiremock.helpers.TestRunnerConfig;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static com.abadeksvp.integrationteststoolkit.wiremock.WireMockJsonVerifySpec.requestedFor;
import static com.abadeksvp.integrationteststoolkit.wiremock.WireMockVerifier.verify;
import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest(classes = TestRunnerConfig.class)
@AutoConfigureWireMock(port = 0)
public class WiremockVerifierTest {

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    private ResourceReader resourceReader;

    @BeforeEach
    void setUp() {
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        resourceReader = new ClasspathResourceReader();
    }

    @Test
    @DisplayName("Simple GET request verified correctly")
    void verifyEmptyGetRequestTest() {
        stubFor(WireMock.get("/test")
                .willReturn(okForJson("""
                        {
                            "test": "test"
                        }
                        """))
        );

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("http://localhost:" + wiremockPort + "/test",
                String.class);

        verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
        );
    }

    @Test
    @DisplayName("GET request with insufficient number of interactions throws correct exception")
    void verifyInsufficientNumberOfInteractions() {
        stubFor(WireMock.get("/test")
                .willReturn(ok())
        );

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("http://localhost:" + wiremockPort + "/test",
                String.class);

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(2))
                ))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""
                        Expected exactly 2 requests matching the following pattern but received 1:
                        {\r
                          "url" : "/test",\r
                          "method" : "GET"\r
                        }""");
    }

    @Test
    @DisplayName("Async request will be awaited and verified correctly")
    void verifyAwaitWorksCorrectlyTest() {
        stubFor(WireMock.get("/test")
                .willReturn(ok())
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForEntity("http://localhost:" + wiremockPort + "/test",
                    String.class);
        });

        verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
                .withWaitDuration(Duration.ofSeconds(5))
        );
    }

    @Test
    @DisplayName("Async request will be awaited and throws correct exception when verification fails")
    void verifyAsyncAwaitWorksCorrectlyWithCorrectExceptionTest() {
        stubFor(WireMock.get("/test")
                .willReturn(ok())
        );

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withWaitDuration(Duration.ofSeconds(1))
                ))
                .isInstanceOf(ConditionTimeoutException.class)
                .hasMessage("""
                        Assertion condition defined as a com.abadeksvp.integrationteststoolkit.wiremock.WireMockVerifier Expected at least one request matching: {\r
                          "url" : "/test",\r
                          "method" : "GET"\r
                        }
                        Requests received: [ ] within 1 seconds.""");
    }

    @Test
    @DisplayName("Unverified request body throws correct exception for GET request")
    void verifyEmptyGetRequestWhenExpectedRequestBodyTest() {
        stubFor(WireMock.get("/test")
                .willReturn(okForJson("""
                        {
                            "test": "test"
                        }
                        """))
        );

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("http://localhost:" + wiremockPort + "/test", String.class);

        assertThrows(VerificationException.class,
                () -> verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withJsonBody("""
                                {
                                    "test": "test"
                                }
                                """)
                ));
    }

    @Test
    @DisplayName("Unverified request body from resource throws correct exception for GET request")
    void verifyEmptyGetRequestWhenExpectedRequestBodyFromResourceTest() {
        stubFor(WireMock.get("/test")
                .willReturn(okForJson("""
                        {
                            "test": "test"
                        }
                        """))
        );

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("http://localhost:" + wiremockPort + "/test", String.class);

        assertThrows(VerificationException.class,
                () -> verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withJsonBody(resourceReader.readString("/test-request-body.json"))
                ));
    }

    @Test
    @DisplayName("Headers verified correctly for GET request")
    void verifyEmptyGetRequestWithHeadersTest() {
        stubFor(WireMock.get("/test")
                .willReturn(okForJson("""
                        {
                            "test": "test"
                        }
                        """))
        );

        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader("Test-Header", "test")
                .build();
        restTemplate.getForEntity("http://localhost:" + wiremockPort + "/test", String.class);
        verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
                .withHeader("Test-Header", equalTo("test"))
        );

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withHeader("Test-Header", equalTo("test2"))))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""
                        No requests exactly matched. Most similar request was:  expected:<
                        GET
                        /test
                                                
                        Test-Header: test2
                        > but was:<
                        GET
                        /test
                                                
                        Test-Header: test
                        >""");
    }

    @Test
    @DisplayName("POST request with custom comparator verified correctly")
    public void verifyPostRequestWithCustomerComparator() {
        stubFor(post("/test")
                .willReturn(okForJson("""
                        {
                            "key1": "key1",
                            "key2": "key2",
                            "key3": "key3",
                            "key4": "key4"
                        }
                        """))
        );

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.postForEntity("http://localhost:" + wiremockPort + "/test", """
                {
                    "key1": "key1",
                    "key2": "invalid",
                    "key3": "key3",
                    "key4": "key4"
                }
                """, String.class);
        verify(requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
                .withJsonBody("""
                                {
                                    "key1": "key1",
                                    "key2": "key2",
                                    "key3": "key3",
                                    "key4": "key4"
                                }
                                """,
                        JsonAssertUtils.withCompareRules(JSONCompareMode.STRICT, "key2"))
        );

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withJsonBody("""
                                {
                                    "key1": "key1",
                                    "key2": "key2",
                                    "key3": "key3",
                                    "key4": "key4"
                                }
                                """)))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""                      
                        No requests exactly matched. Most similar request was:  expected:<
                        POST
                        /test
                                                
                        {
                            "key1": "key1",
                            "key2": "key2",
                            "key3": "key3",
                            "key4": "key4"
                        }
                        > but was:<
                        POST
                        /test
                                                
                        {
                            "key1": "key1",
                            "key2": "invalid",
                            "key3": "key3",
                            "key4": "key4"
                        }
                        >""");
    }

    @Test
    @DisplayName("POST request with verification of non-json body")
    void verifyPostRequestWithNonJsonBody() {
        stubFor(post("/test")
                .willReturn(ok("Test non-json response"))
        );

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.postForEntity("http://localhost:" + wiremockPort + "/test", "Non-json request.", String.class);
        verify(
                requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withTextBody(equalTo("Non-json request."))
        );

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withTextBody(equalTo("Non-json request2."))
                ))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""
                        No requests exactly matched. Most similar request was:  expected:<
                        POST
                        /test

                        Non-json request2.> but was:<
                        POST
                        /test

                        Non-json request.>"""
                );
    }

    @Test
    @DisplayName("POST request with verification of json body when the requested body is not json")
    void verifyPostRequestWithJsonBodyWhenRequestedBodyIsNotJson() {
        stubFor(post("/test")
                .willReturn(ok("Test non-json response"))
        );

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.postForEntity("http://localhost:" + wiremockPort + "/test", "Non-json request.", String.class);

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withJsonBody("""
                                {
                                    "key1": "val1"
                                }
                                """)
                ))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""
                        No requests exactly matched. Most similar request was:  expected:<
                        POST
                        /test
                                                
                        {
                            "key1": "val1"
                        }
                        > but was:<
                        POST
                        /test
                                                
                        Non-json request.>"""
                );
    }

    @Test
    @DisplayName("POST request to the same endpoint but with different bodies verified correctly")
    public void verifyPostRequestWithDifferentBodies() {
        stubFor(post("/test")
                .willReturn(okForJson("""
                        {
                            "key1": "key1"
                        }
                        """))
        );

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        //first interaction
        restTemplate.postForEntity("http://localhost:" + wiremockPort + "/test", """
                {
                    "key1": "val1"
                }
                """, String.class);

        //second interaction
        restTemplate.postForEntity("http://localhost:" + wiremockPort + "/test", """
                {
                    "key2": "val2"
                }
                """, String.class);

        verify(requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(2))
        );

        assertThatThrownBy(
                () -> verify(requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(2))
                        .withJsonBody("""
                                {
                                    "key1": "val1"
                                }
                                """)))
                .isInstanceOf(VerificationException.class)
                .hasMessage("""
                        Expected exactly 2 requests matching the following pattern but received 1:
                        {\r
                          "url" : "/test",\r
                          "method" : "POST"\r
                        }""");
    }
}
