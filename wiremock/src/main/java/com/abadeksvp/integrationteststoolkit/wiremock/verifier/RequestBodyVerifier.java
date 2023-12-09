package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import com.abadeksvp.integrationteststoolkit.wiremock.WireMockVerificationSpec;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

/**
 * The RequestBodyVerifier interface represents a verifier for request bodies in WireMock requests.
 */
public interface RequestBodyVerifier {

    /**
     * Verifies the requests based on the given WireMockVerificationSpec object.
     *
     * @param spec                 the WireMockVerificationSpec object specifying the verification criteria
     * @param requests             the list of logged requests to verify
     * @param requestPatternBuilder the request pattern builder used to define the request criteria
     */
    void verify(WireMockVerificationSpec spec, List<LoggedRequest> requests, RequestPatternBuilder requestPatternBuilder);
}
