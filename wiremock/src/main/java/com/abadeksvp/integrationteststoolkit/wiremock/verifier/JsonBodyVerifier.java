package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.JSONParser;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.abadeksvp.integrationteststoolkit.wiremock.WireMockVerificationSpec;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import com.github.tomakehurst.wiremock.verification.diff.Diff;

import lombok.Getter;
import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;

public class JsonBodyVerifier implements RequestBodyVerifier {

    @Getter
    private final String content;
    private final CustomComparator customComparator;

    public JsonBodyVerifier(String content, CustomComparator customComparator) {
        this.content = content;
        this.customComparator = customComparator;
    }

    @Override
    @SneakyThrows
    public void verify(WireMockVerificationSpec spec, List<LoggedRequest> requests,
            RequestPatternBuilder requestPatternBuilder) {
        String expectedRequest = content;
        if (expectedRequest == null) {
            return;
        }
        int actualMatched = 0;
        for (LoggedRequest request : requests) {
            String actualRequest = request.getBodyAsString();
            if (StringUtils.isEmpty(actualRequest)) {
                continue;
            }
            if (!isJson(actualRequest) || !isJson(expectedRequest)) {
                continue;
            }
            JSONCompareResult result = JSONCompare.compareJSON(expectedRequest, actualRequest, this.customComparator);
            if (result.passed()) {
                actualMatched++;
            }
        }
        if (!spec.getNumberOfInteractions().match(actualMatched)) {
            throw buildCountValidationException(spec, expectedRequest, requestPatternBuilder, actualMatched);
        }
    }

    private boolean isJson(String content) {
        try {
            JSONParser.parseJSON(content);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private VerificationException buildCountValidationException(WireMockVerificationSpec spec,
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
