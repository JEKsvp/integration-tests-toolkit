package com.abadeksvp.integrationteststoolkit.wiremock;

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
import com.abadeksvp.integrationteststoolkit.wiremock.helpers.TestRunnerConfig;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest(classes = TestRunnerConfig.class)
@AutoConfigureWireMock(port = 0)
public class WiremockVerifierTest {

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    private WireMockVerifier verifier;

    @BeforeEach
    void setUp() {
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        verifier = new WireMockVerifier();
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

        verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
        );
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
                () -> verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withRequest("""
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
                () -> verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withRequestFromResource("/test-request-body.json")
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
        verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.GET, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
                .withHeader("Test-Header", equalTo("test"))
        );

        assertThatThrownBy(
                () -> verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.GET, urlEqualTo("/test"))
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
        verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                .withNumberOfInteractions(exactly(1))
                .withRequest("""
                        {
                            "key1": "key1",
                            "key2": "key2",
                            "key3": "key3",
                            "key4": "key4"
                        }
                        """)
                .withCustomComparator(JsonAssertUtils.withCompareRules(JSONCompareMode.STRICT,
                        "key2"))
        );

        assertThatThrownBy(
                () -> verifier.verify(WireMockJsonVerifySpec.requestedFor(RequestMethod.POST, urlEqualTo("/test"))
                        .withNumberOfInteractions(exactly(1))
                        .withRequest("""
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
                .willReturn(okForJson("Test non-json response"))
        );

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.postForEntity("http://localhost:" + wiremockPort + "/test", "Non-json request.", String.class);
        verifier.verify(
                WireMockJsonVerifySpec.requestedFor(RequestMethod.POST, urlEqualTo("/test"), RequestBodyType.TEXT)
                        .withNumberOfInteractions(exactly(1))
                        .withRequest("Non-json request.")
        );

        assertThatThrownBy(
                () -> verifier.verify(
                        WireMockJsonVerifySpec.requestedFor(RequestMethod.POST, urlEqualTo("/test"),
                                        RequestBodyType.TEXT)
                                .withNumberOfInteractions(exactly(1))
                                .withRequest("Non-json request2.")
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
}
