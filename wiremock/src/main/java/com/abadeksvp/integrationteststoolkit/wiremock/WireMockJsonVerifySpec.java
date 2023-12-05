package com.abadeksvp.integrationteststoolkit.wiremock;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.abadeksvp.integrationteststoolkit.wiremock.verifier.JsonBodyVerifier;
import com.abadeksvp.integrationteststoolkit.wiremock.verifier.RequestBodyVerifier;
import com.abadeksvp.integrationteststoolkit.wiremock.verifier.TextBodyVerifier;
import com.abadeksvp.integrationteststoolkit.wiremock.verifier.XmlBodyVerifier;
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.EqualToXmlPattern;
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
    private RequestBodyVerifier requestBodyVerifier;
    private Duration waitDuration;

    private WireMockJsonVerifySpec() {
    }

    public static WireMockJsonVerifySpec requestedFor(RequestMethod httpMethod, UrlPattern urlPattern) {
        WireMockJsonVerifySpec spec = new WireMockJsonVerifySpec();
        spec.setHttpMethod(httpMethod);
        spec.setUrlPattern(urlPattern);
        return spec;
    }

    public WireMockJsonVerifySpec withNumberOfInteractions(CountMatchingStrategy numberOfInteractions) {
        this.numberOfInteractions = numberOfInteractions;
        return this;
    }

    public WireMockJsonVerifySpec withWaitDuration(Duration timeout) {
        this.waitDuration = timeout;
        return this;
    }

    public WireMockJsonVerifySpec withHeader(String headerName, StringValuePattern headerValue) {
        this.headers.put(headerName, headerValue);
        return this;
    }

    public WireMockJsonVerifySpec withJsonBody(String content) {
        this.requestBodyVerifier = new JsonBodyVerifier(content, new CustomComparator(JSONCompareMode.STRICT));
        return this;
    }

    public WireMockJsonVerifySpec withJsonBody(String content, CustomComparator customComparator) {
        this.requestBodyVerifier = new JsonBodyVerifier(content, customComparator);
        return this;
    }

    public WireMockJsonVerifySpec withTextBody(ContentPattern contentPattern) {
        this.requestBodyVerifier = new TextBodyVerifier(contentPattern);
        return this;
    }

    public WireMockJsonVerifySpec withXmlBody(EqualToXmlPattern xmlPattern) {
        this.requestBodyVerifier = new XmlBodyVerifier(xmlPattern);
        return this;
    }

    public WireMockJsonVerifySpec withCustomBody(RequestBodyVerifier requestBodyVerifier) {
        this.requestBodyVerifier = requestBodyVerifier;
        return this;
    }
}
