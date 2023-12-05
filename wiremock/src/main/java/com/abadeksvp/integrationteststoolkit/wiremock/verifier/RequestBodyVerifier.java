package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import com.abadeksvp.integrationteststoolkit.wiremock.WireMockJsonVerifySpec;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

public interface RequestBodyVerifier {

    void verify(WireMockJsonVerifySpec spec, List<LoggedRequest> requests, RequestPatternBuilder requestPatternBuilder);
}
