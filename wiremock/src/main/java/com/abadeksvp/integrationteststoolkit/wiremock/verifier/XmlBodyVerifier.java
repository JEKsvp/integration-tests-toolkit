package com.abadeksvp.integrationteststoolkit.wiremock.verifier;

import java.util.List;

import com.abadeksvp.integrationteststoolkit.wiremock.WireMockVerificationSpec;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToXmlPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

public class XmlBodyVerifier implements RequestBodyVerifier {

    private final EqualToXmlPattern equalToXmlPattern;

    public XmlBodyVerifier(EqualToXmlPattern equalToXmlPattern) {
        this.equalToXmlPattern = equalToXmlPattern;
    }

    @Override
    public void verify(WireMockVerificationSpec spec, List<LoggedRequest> requests,
            RequestPatternBuilder requestPatternBuilder) {
        WireMock.verify(requestPatternBuilder.withRequestBody(equalToXmlPattern));
    }
}
