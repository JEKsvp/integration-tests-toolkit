package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import com.abadeksvp.integrationteststoolkit.resource.ResourceReader;
import com.abadeksvp.integrationteststoolkit.wiremock.WireMockJsonVerifySpec;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class TextBodyVerifier implements RequestBodyVerifier {

    private final ResourceReader resourceReader;

    public TextBodyVerifier(ResourceReader resourceReader) {
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
        verify(requestPatternBuilder.withRequestBody(equalTo(expectedRequest)));
    }

    private String defineExpectedResponse(WireMockJsonVerifySpec spec) {
        if (spec.getExpectedResourceName() != null) {
            return resourceReader.readString(spec.getExpectedResourceName());
        } else if (spec.getExpectedRequest() != null) {
            return spec.getExpectedRequest();
        }
        return null;
    }
}
