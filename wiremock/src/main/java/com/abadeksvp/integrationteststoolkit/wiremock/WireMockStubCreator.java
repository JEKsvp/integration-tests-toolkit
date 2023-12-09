package com.abadeksvp.integrationteststoolkit.wiremock;

import com.abadeksvp.integrationteststoolkit.resource.ClasspathResourceReader;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;


/**
 * The WireMockStubCreator class provides methods to create stub mappings for WireMock server.
 */
@Slf4j
public class WireMockStubCreator {

    @Setter
    private ClasspathResourceReader resourceReader = new ClasspathResourceReader();

    @Setter
    private HttpHeaders defaultHeaders = new HttpHeaders();

    public StubMapping createEmptyStub(RequestMethod httpMethod, UrlPattern urlPattern) {
        return WireMock.stubFor(WireMock.request(httpMethod.getName(), urlPattern)
                .willReturn(responseDefinition()
                        .withHeaders(defaultHeaders)
                        .withStatus(200)
                ));
    }

    public StubMapping createStub(RequestMethod httpMethod, UrlPattern urlPattern, String responseBody) {
        return WireMock.stubFor(WireMock.request(httpMethod.getName(), urlPattern)
                .willReturn(responseDefinition()
                        .withHeaders(defaultHeaders)
                        .withStatus(200)
                        .withBody(responseBody)
                ));
    }

    public StubMapping createStubFromResource(RequestMethod httpMethod, UrlPattern urlPattern, String resourceName) {
        return WireMock.stubFor(WireMock.request(httpMethod.getName(), urlPattern)
                .willReturn(responseDefinition()
                        .withHeaders(defaultHeaders)
                        .withStatus(200)
                        .withBody(resourceReader.readString(resourceName))
                ));
    }
}