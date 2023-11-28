package com.abadeksvp.integrationteststoolkit.wiremock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.abadeksvp.integrationteststoolkit.resource.ClasspathResourceReader;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.abadeksvp.integrationteststoolkit.JsonAssertUtils.withCompareRules;
import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;


@Slf4j
public class WireMockShortcut {

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

    public void verifyRequestWithResource(RequestMethod httpMethod, UrlPattern urlPattern, String resourceName) {
        verifyRequest(httpMethod, urlPattern, resourceReader.readString(resourceName));
    }

    public void verifyRequestWithResource(RequestMethod httpMethod, UrlPattern urlPattern, String resourceName,
            String... ignoredFields) {
        verifyRequest(httpMethod, urlPattern, resourceReader.readString(resourceName),
                withCompareRules(JSONCompareMode.STRICT, ignoredFields));
    }

    public void verifyRequestWithResource(RequestMethod httpMethod, UrlPattern urlPattern, String resourceName,
            CustomComparator comparator) {
        verifyRequest(httpMethod, urlPattern, resourceReader.readString(resourceName), comparator);
    }

    public void verifyRequest(RequestMethod httpMethod, UrlPattern urlPattern, String expectedResponse,
            String... ignoredFields) {
        verifyRequest(httpMethod, urlPattern, expectedResponse,
                withCompareRules(JSONCompareMode.STRICT, ignoredFields));
    }

    public void verifyRequest(RequestMethod httpMethod, UrlPattern urlPattern, String expectedResponse,
            CustomComparator comparator) {
        if (httpMethod == RequestMethod.GET) {
            verifyRequest(WireMock.getRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.POST) {
            verifyRequest(WireMock.postRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.PATCH) {
            verifyRequest(WireMock.patchRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.PUT) {
            verifyRequest(WireMock.putRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.DELETE) {
            verifyRequest(WireMock.deleteRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.HEAD) {
            verifyRequest(WireMock.headRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.OPTIONS) {
            verifyRequest(WireMock.optionsRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.TRACE) {
            verifyRequest(WireMock.traceRequestedFor(urlPattern), expectedResponse, comparator);
        } else if (httpMethod == RequestMethod.ANY) {
            verifyRequest(WireMock.anyRequestedFor(urlPattern), expectedResponse, comparator);
        }
    }

    @SneakyThrows
    public void verifyRequest(RequestPatternBuilder requestPatternBuilder, String expectedJson,
            CustomComparator comparator) {
        List<LoggedRequest> requests = WireMock.findAll(requestPatternBuilder);
        List<String> requestBodies = new ArrayList<>();
        for (LoggedRequest request : requests) {
            String requestBody = request.getBodyAsString();
            requestBodies.add(requestBody);
            JSONCompareResult result = JSONCompare.compareJSON(expectedJson, requestBody, comparator);
            if (result.passed()) {
                return;
            }
        }
        throw new VerificationException(
                String.format("Request with body:\n %s \n was not received. received requests:\n %s",
                        expectedJson, Arrays.toString(requestBodies.toArray())));
    }

    private String readResource(String resourceName) {
        return resourceReader.readString(resourceName);
    }
}