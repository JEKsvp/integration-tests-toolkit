package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import com.abadeksvp.integrationteststoolkit.wiremock.WireMockVerificationSpec;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

public class TextBodyVerifier implements RequestBodyVerifier {

    private final ContentPattern content;

    public TextBodyVerifier(ContentPattern content) {
        this.content = content;
    }

    @Override
    public void verify(WireMockVerificationSpec spec, List<LoggedRequest> requests,
            RequestPatternBuilder requestPatternBuilder) {
        if (content == null) {
            return;
        }
        WireMock.verify(requestPatternBuilder.withRequestBody(content));
    }
}
