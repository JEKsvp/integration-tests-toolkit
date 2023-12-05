package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareResult;

import com.abadeksvp.integrationteststoolkit.resource.ResourceReader;
import com.abadeksvp.integrationteststoolkit.wiremock.WireMockJsonVerifySpec;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import com.github.tomakehurst.wiremock.verification.diff.Diff;

import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;

public class JsonBodyVerifier implements RequestBodyVerifier {

    private final ResourceReader resourceReader;

    public JsonBodyVerifier(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    @SneakyThrows
    public void verifyRequestBody(WireMockJsonVerifySpec spec, List<LoggedRequest> requests,
            RequestPatternBuilder requestPatternBuilder) {
        String expectedRequest = defineExpectedResponse(spec);
        if (expectedRequest == null) {
            return;
        }
        int actualMatched = 0;
        for (LoggedRequest request : requests) {
            String actualRequest = request.getBodyAsString();
            if (StringUtils.isEmpty(actualRequest)) {
                continue;
            }
            JSONCompareResult result = JSONCompare.compareJSON(expectedRequest, actualRequest,
                    spec.getCustomComparator());
            if (result.passed()) {
                actualMatched++;
            }
        }
        if (!spec.getNumberOfInteractions().match(actualMatched)) {
            throw buildCountValidationException(spec, expectedRequest, requestPatternBuilder, actualMatched);
        }
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
            String expectedRequest, RequestPatternBuilder requestPatternBuilder, int actualCount) {
        RequestPattern requestPattern = requestPatternBuilder.build();
        return actualCount == 0
                ? verificationExceptionForNearMisses(requestPatternBuilder, expectedRequest)
                : new VerificationException(requestPattern, spec.getNumberOfInteractions(), actualCount);
    }

    private VerificationException verificationExceptionForNearMisses(RequestPatternBuilder requestPatternBuilder,
            String expectedRequest) {
        RequestPattern requestPattern = requestPatternBuilder.withRequestBody(WireMock.equalTo(expectedRequest))
                .build();
        List<NearMiss> nearMisses = WireMock.findNearMissesFor(requestPatternBuilder);
        if (nearMisses.size() > 0) {
            Diff diff = new Diff(requestPattern, nearMisses.get(0).getRequest());
            return VerificationException.forUnmatchedRequestPattern(diff);
        }

        return new VerificationException(requestPattern, WireMock.findAll(allRequests()));
    }
}
