package com.abadeksvp.integrationteststoolkit.wiremock;

import java.util.List;
import java.util.Map;

import org.awaitility.Awaitility;

import com.abadeksvp.integrationteststoolkit.resource.ClasspathResourceReader;
import com.abadeksvp.integrationteststoolkit.resource.ResourceReader;
import com.abadeksvp.integrationteststoolkit.wiremock.verifier.JsonBodyVerifier;
import com.abadeksvp.integrationteststoolkit.wiremock.verifier.RequestBodyVerifier;
import com.abadeksvp.integrationteststoolkit.wiremock.verifier.TextBodyVerifier;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import com.github.tomakehurst.wiremock.verification.diff.Diff;

import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.http.RequestMethod.DELETE;
import static com.github.tomakehurst.wiremock.http.RequestMethod.GET;
import static com.github.tomakehurst.wiremock.http.RequestMethod.PATCH;
import static com.github.tomakehurst.wiremock.http.RequestMethod.POST;
import static com.github.tomakehurst.wiremock.http.RequestMethod.PUT;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;

public class WireMockVerifier {

    private final ResourceReader resourceReader;
    private final Map<RequestBodyType, RequestBodyVerifier> requestBodyVerifiers;

    public WireMockVerifier() {
        this(new ClasspathResourceReader());
    }

    public WireMockVerifier(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
        this.requestBodyVerifiers = Map.of(
                RequestBodyType.JSON, new JsonBodyVerifier(resourceReader),
                RequestBodyType.XML, new TextBodyVerifier(resourceReader), //todo implement XML verifier
                RequestBodyType.TEXT, new TextBodyVerifier(resourceReader)
        );
    }

    public void verify(WireMockJsonVerifySpec spec) {
        if (spec.getWaitDuration() != null) {
            Awaitility.await().atMost(spec.getWaitDuration()).untilAsserted(() -> {
                verifyInternal(spec);
            });
        } else {
            verifyInternal(spec);
        }
    }

    @SneakyThrows
    private void verifyInternal(WireMockJsonVerifySpec spec) {
        RequestPatternBuilder requestPatternBuilder = defineRequestPattern(spec);

        List<LoggedRequest> requests = WireMock.findAll(requestPatternBuilder);
        if (!spec.getNumberOfInteractions().match(requests.size())) {
            throw buildCountValidationException(spec, requestPatternBuilder, requests.size());
        }

        RequestBodyVerifier bodyVerifier = requestBodyVerifiers.get(spec.getRequestBodyType());
        if (bodyVerifier == null) {
            throw new IllegalArgumentException("Unsupported request body type: " + spec.getRequestBodyType());
        }
        bodyVerifier.verifyRequestBody(spec, requests, requestPatternBuilder);
    }

    private String defineExpectedResponse(WireMockJsonVerifySpec spec) {
        if (spec.getExpectedResourceName() != null) {
            return resourceReader.readString(spec.getExpectedResourceName());
        } else if (spec.getExpectedRequest() != null) {
            return spec.getExpectedRequest();
        }
        return null;
    }

    private VerificationException buildCountValidationException(WireMockJsonVerifySpec spec,
            RequestPatternBuilder requestPatternBuilder, int actualCount) {
        RequestPattern requestPattern = requestPatternBuilder.build();
        return actualCount == 0
                ? verificationExceptionForNearMisses(requestPatternBuilder)
                : new VerificationException(requestPattern, spec.getNumberOfInteractions(), actualCount);
    }

    private VerificationException verificationExceptionForNearMisses(RequestPatternBuilder requestPatternBuilder) {
        RequestPattern requestPattern = requestPatternBuilder.build();
        List<NearMiss> nearMisses = WireMock.findNearMissesFor(requestPatternBuilder);
        if (nearMisses.size() > 0) {
            Diff diff = new Diff(requestPattern, nearMisses.get(0).getRequest());
            return VerificationException.forUnmatchedRequestPattern(diff);
        }

        return new VerificationException(requestPattern, WireMock.findAll(allRequests()));
    }

    private static RequestPatternBuilder defineRequestPattern(WireMockJsonVerifySpec spec) {
        RequestPatternBuilder patternBuilder;
        if (spec.getHttpMethod().equals(GET)) {
            patternBuilder = WireMock.getRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(POST)) {
            patternBuilder = WireMock.postRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(PATCH)) {
            patternBuilder = WireMock.patchRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(PUT)) {
            patternBuilder = WireMock.putRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(DELETE)) {
            patternBuilder = WireMock.deleteRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.HEAD)) {
            patternBuilder = WireMock.headRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.OPTIONS)) {
            patternBuilder = WireMock.optionsRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.TRACE)) {
            patternBuilder = WireMock.traceRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.ANY)) {
            patternBuilder = WireMock.anyRequestedFor(spec.getUrlPattern());
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + spec.getHttpMethod());
        }

        spec.getHeaders().forEach(patternBuilder::withHeader);
        return patternBuilder;
    }
}
