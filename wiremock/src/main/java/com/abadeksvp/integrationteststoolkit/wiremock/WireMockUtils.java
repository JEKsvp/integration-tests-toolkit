package com.abadeksvp.integrationteststoolkit.wiremock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.abadeksvp.integrationteststoolkit.ResourceReader;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.abadeksvp.integrationteststoolkit.JsonAssertUtils.withCompareRules;
import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;


@SuppressWarnings("unused")
@Slf4j
public class WireMockUtils {

    public static final String EMPTY_RESPONSE = "";

    private static final ResourceReader resourceReader = new ResourceReader();

    public static void createEmptyStub(RequestMethod httpMethod, UrlPattern urlPattern) {
        WireMock.stubFor(WireMock.request(httpMethod.getName(), urlPattern)
                .willReturn(responseDefinition()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                ));
    }

    public static void createStubFromFile(RequestMethod httpMethod, UrlPattern urlPattern, String resourceName) {
        WireMock.stubFor(WireMock.request(httpMethod.getName(), urlPattern)
                .willReturn(responseDefinition()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile(resourceReader.readString(resourceName))
                ));
    }

    public static void createStub(RequestMethod httpMethod, UrlPattern urlPattern, String responseBody) {
        WireMock.stubFor(WireMock.request(httpMethod.getName(), urlPattern)
                .willReturn(responseDefinition()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(responseBody)
                ));
    }

    public static void verifyRequestFromFile(RequestMethod httpMethod, UrlPattern urlPattern, String resourceName) {
        verifyRequest(httpMethod, urlPattern, resourceReader.readString(resourceName));
    }

    public static void verifyRequest(RequestMethod httpMethod, UrlPattern urlPattern, String expectedResponse,
            String... ignoredFields) {
        verifyRequest(httpMethod, urlPattern, expectedResponse,
                withCompareRules(JSONCompareMode.STRICT, ignoredFields));
    }

    public static void verifyRequest(RequestMethod httpMethod, UrlPattern urlPattern, String expectedResponse,
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
    public static void verifyRequest(RequestPatternBuilder requestPatternBuilder, String expectedJson,
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

    private static String readResource(String resourceName) {
        return resourceReader.readString(resourceName);
    }
}