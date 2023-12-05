package com.abadeksvp.integrationteststoolkit.wiremock;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Setter(AccessLevel.PRIVATE)
public class WireMockJsonVerifySpec {

    private CountMatchingStrategy numberOfInteractions = new CountMatchingStrategy(CountMatchingStrategy.EQUAL_TO, 1);
    private RequestMethod httpMethod;
    private UrlPattern urlPattern;
    private Map<String, StringValuePattern> headers = new HashMap<>();
    private CustomComparator customComparator = new CustomComparator(JSONCompareMode.STRICT);
    private RequestBodyType requestBodyType = RequestBodyType.JSON;
    private String expectedRequest;
    private String expectedResourceName;
    private Duration waitDuration;

    private WireMockJsonVerifySpec() {
    }

    public static WireMockJsonVerifySpec requestedFor(RequestMethod httpMethod, UrlPattern urlPattern) {
        WireMockJsonVerifySpec spec = new WireMockJsonVerifySpec();
        spec.setHttpMethod(httpMethod);
        spec.setUrlPattern(urlPattern);
        return spec;
    }

    public static WireMockJsonVerifySpec requestedFor(RequestMethod httpMethod, UrlPattern urlPattern,
            RequestBodyType bodyType) {
        WireMockJsonVerifySpec spec = new WireMockJsonVerifySpec();
        spec.setHttpMethod(httpMethod);
        spec.setUrlPattern(urlPattern);
        spec.setRequestBodyType(bodyType);
        return spec;
    }

    public WireMockJsonVerifySpec withNumberOfInteractions(CountMatchingStrategy numberOfInteratcions) {
        this.numberOfInteractions = numberOfInteratcions;
        return this;
    }

    public WireMockJsonVerifySpec withRequest(String expectedResponse) {
        this.expectedRequest = expectedResponse;
        return this;
    }

    public WireMockJsonVerifySpec withRequestFromResource(String expectedResourceName) {
        this.expectedResourceName = expectedResourceName;
        return this;
    }

    public WireMockJsonVerifySpec withWaitDuration(Duration timeout) {
        this.waitDuration = timeout;
        return this;
    }

    public WireMockJsonVerifySpec withCustomComparator(CustomComparator jsonCompareMode) {
        this.customComparator = jsonCompareMode;
        return this;
    }

    public WireMockJsonVerifySpec withHeader(String headerName, StringValuePattern headerValue) {
        this.headers.put(headerName, headerValue);
        return this;
    }

    public WireMockJsonVerifySpec withRequestBodyType(RequestBodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
        return this;
    }
}
