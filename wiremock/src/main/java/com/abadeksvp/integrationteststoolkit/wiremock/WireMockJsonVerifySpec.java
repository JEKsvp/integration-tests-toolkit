package com.abadeksvp.integrationteststoolkit.wiremock;

import java.time.Duration;

import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Setter(AccessLevel.PRIVATE)
public class WireMockJsonVerifySpec {

    @Builder.Default
    private CountMatchingStrategy numberOfInteractions = new CountMatchingStrategy(CountMatchingStrategy.EQUAL_TO, 1);

    private RequestMethod httpMethod;
    private UrlPattern urlPattern;

    @Builder.Default
    private CustomComparator customComparator = new CustomComparator(JSONCompareMode.STRICT);

    private String expectedResponse;
    private String expectedResourceName;

    @Builder.Default
    private Duration waitDuration = Duration.ofSeconds(10);

    private WireMockJsonVerifySpec() {
    }

    public static WireMockJsonVerifySpec create(RequestMethod httpMethod, UrlPattern urlPattern) {
        WireMockJsonVerifySpec spec = new WireMockJsonVerifySpec();
        spec.setHttpMethod(httpMethod);
        spec.setUrlPattern(urlPattern);
        return spec;
    }

    public WireMockJsonVerifySpec withNumberOfInteractions(CountMatchingStrategy numberOfInteratcions) {
        this.numberOfInteractions = numberOfInteratcions;
        return this;
    }

    public WireMockJsonVerifySpec withResponse(String expectedResponse) {
        this.expectedResponse = expectedResponse;
        return this;
    }

    public WireMockJsonVerifySpec withResponseFromResource(String expectedResourceName) {
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
}