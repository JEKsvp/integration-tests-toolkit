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

/**
 * The WireMockVerificationSpec class represents the criteria for verifying requests made to WireMock.
 * It allows specifying the HTTP method, URL pattern, number of interactions, headers, and request body verifier.
 */
@Getter
@EqualsAndHashCode
@ToString
@Setter(AccessLevel.PRIVATE)
public class WireMockVerificationSpec {

    private CountMatchingStrategy numberOfInteractions = new CountMatchingStrategy(CountMatchingStrategy.EQUAL_TO, 1);
    private RequestMethod httpMethod;
    private UrlPattern urlPattern;
    private Map<String, StringValuePattern> headers = new HashMap<>();
    private RequestBodyVerifier requestBodyVerifier;
    private Duration waitDuration;

    private WireMockVerificationSpec() {
    }

    /**
     * Creates a new WireMockVerificationSpec object to specify the verification criteria for requests.
     *
     * @param httpMethod the HTTP method of the request
     * @param urlPattern the pattern for the URL of the request
     * @return a WireMockVerificationSpec object with the specified criteria
     */
    public static WireMockVerificationSpec requestedFor(RequestMethod httpMethod, UrlPattern urlPattern) {
        WireMockVerificationSpec spec = new WireMockVerificationSpec();
        spec.setHttpMethod(httpMethod);
        spec.setUrlPattern(urlPattern);
        return spec;
    }

    /**
     * Sets the number of interactions to be verified for a request.
     *
     * @param numberOfInteractions the count matching strategy for the number of interactions to be verified
     * @return the WireMockVerificationSpec object with the specified number of interactions
     */
    public WireMockVerificationSpec withNumberOfInteractions(CountMatchingStrategy numberOfInteractions) {
        this.numberOfInteractions = numberOfInteractions;
        return this;
    }

    /**
     * Sets the maximum duration to wait for specified interactions to be verified. Can be used for asynchronous
     * requests that can be completed after the test method has finished.
     *
     * @param timeout the maximum duration to wait
     * @return the WireMockVerificationSpec object
     */
    public WireMockVerificationSpec waitAtMost(Duration timeout) {
        this.waitDuration = timeout;
        return this;
    }

    /**
     * Adds a header with the given name and value pattern to the WireMockVerificationSpec object.
     *
     * @param headerName  the name of the header
     * @param headerValue the value pattern of the header
     * @return a WireMockVerificationSpec object with the header added
     */
    public WireMockVerificationSpec withHeader(String headerName, StringValuePattern headerValue) {
        this.headers.put(headerName, headerValue);
        return this;
    }

    /**
     * Sets the JSON body verifier for the WireMockVerificationSpec object.
     *
     * @param content the JSON content of the request body
     * @return the WireMockVerificationSpec object with the JSON body verifier
     */
    public WireMockVerificationSpec withJsonBody(String content) {
        this.requestBodyVerifier = new JsonBodyVerifier(content, new CustomComparator(JSONCompareMode.STRICT));
        return this;
    }

    /**
     * Sets the JSON body verifier for the WireMockVerificationSpec object.
     *
     * @param content          the JSON content of the request body
     * @param customComparator the custom comparator to use for JSON comparison
     * @return the WireMockVerificationSpec object with the JSON body verifier
     */
    public WireMockVerificationSpec withJsonBody(String content, CustomComparator customComparator) {
        this.requestBodyVerifier = new JsonBodyVerifier(content, customComparator);
        return this;
    }

    /**
     * Sets the text body verifier for the WireMockVerificationSpec object.
     *
     * @param contentPattern the content pattern of the text body
     * @return the WireMockVerificationSpec object with the text body verifier
     */
    public WireMockVerificationSpec withTextBody(ContentPattern contentPattern) {
        this.requestBodyVerifier = new TextBodyVerifier(contentPattern);
        return this;
    }

    /**
     * Sets the XML body verifier for the WireMockVerificationSpec object.
     *
     * @param xmlPattern the XML pattern of the request body
     * @return the WireMockVerificationSpec object with the XML body verifier
     */
    public WireMockVerificationSpec withXmlBody(EqualToXmlPattern xmlPattern) {
        this.requestBodyVerifier = new XmlBodyVerifier(xmlPattern);
        return this;
    }

    /**
     * Sets the request body verifier for the WireMockVerificationSpec object.
     *
     * @param requestBodyVerifier the request body verifier to be set
     * @return the WireMockVerificationSpec object with the specified request body verifier
     */
    public WireMockVerificationSpec withBody(RequestBodyVerifier requestBodyVerifier) {
        this.requestBodyVerifier = requestBodyVerifier;
        return this;
    }
}
