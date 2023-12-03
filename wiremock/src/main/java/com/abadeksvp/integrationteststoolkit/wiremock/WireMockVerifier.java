package com.abadeksvp.integrationteststoolkit.wiremock;

import java.util.List;

import org.awaitility.Awaitility;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareResult;

import com.abadeksvp.integrationteststoolkit.resource.ClasspathResourceReader;
import com.abadeksvp.integrationteststoolkit.resource.ResourceReader;
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

    public WireMockVerifier() {
        this.resourceReader = new ClasspathResourceReader();
    }

    public WireMockVerifier(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    public void verify(WireMockJsonVerifySpec spec) {
        if (spec.getWaitDuration() != null) {
            Awaitility.await().atMost(spec.getWaitDuration()).until(() -> {
                try {
                    verifyInternal(spec);
                    return true;
                } catch (VerificationException e) {
                    return false;
                }
            });
        } else {
            verifyInternal(spec);
        }
    }

    @SneakyThrows
    private void verifyInternal(WireMockJsonVerifySpec spec) {
        RequestPatternBuilder requestPatternBuilder = defineRequestPattern(spec);

        List<LoggedRequest> requests = WireMock.findAll(requestPatternBuilder);
        if (!spec.getExpectedCount().match(requests.size())) {
            throw buildCountValidationException(spec, requestPatternBuilder, requests.size());
        }

        String expectedResponse = defineExpectedResponse(spec);
        if (expectedResponse != null) {
            verifyResponseBody(spec, expectedResponse, requests, requestPatternBuilder);
        }
    }

    private String defineExpectedResponse(WireMockJsonVerifySpec spec) {
        if (spec.getExpectedResourceName() != null) {
            return resourceReader.readString(spec.getExpectedResourceName());
        } else if (spec.getExpectedResponse() != null) {
            return spec.getExpectedResponse();
        }
        return null;
    }

    private void verifyResponseBody(WireMockJsonVerifySpec spec, String expectedResponse, List<LoggedRequest> requests,
            RequestPatternBuilder requestPatternBuilder) throws JSONException {
        int actualMatched = 0;
        for (LoggedRequest request : requests) {
            String requestBody = request.getBodyAsString();
            JSONCompareResult result = JSONCompare.compareJSON(expectedResponse, requestBody,
                    spec.getCompareMode());
            if (result.passed()) {
                actualMatched++;
            }
        }
        if (!spec.getExpectedCount().match(actualMatched)) {
            throw buildCountValidationException(spec, requestPatternBuilder, actualMatched);
        }
    }

    private VerificationException buildCountValidationException(WireMockJsonVerifySpec spec,
            RequestPatternBuilder requestPatternBuilder, int actualCount) {
        RequestPattern requestPattern = requestPatternBuilder.build();
        return actualCount == 0
                ? verificationExceptionForNearMisses(requestPatternBuilder)
                : new VerificationException(requestPattern, spec.getExpectedCount(), actualCount);
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
        if (spec.getHttpMethod().equals(GET)) {
            return WireMock.getRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(POST)) {
            return WireMock.postRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(PATCH)) {
            return WireMock.patchRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(PUT)) {
            return WireMock.putRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(DELETE)) {
            return WireMock.deleteRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.HEAD)) {
            return WireMock.headRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.OPTIONS)) {
            return WireMock.optionsRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.TRACE)) {
            return WireMock.traceRequestedFor(spec.getUrlPattern());
        } else if (spec.getHttpMethod().equals(RequestMethod.ANY)) {
            return WireMock.anyRequestedFor(spec.getUrlPattern());
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + spec.getHttpMethod());
        }
    }
}
