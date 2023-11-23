package com.abadeksvp.integrationteststoolkit.wiremock;

import org.apache.http.HttpHeaders;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class NoKeepAliveTransformer extends ResponseDefinitionTransformer {

    private final String NAME = NoKeepAliveTransformer.class.getSimpleName();

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files,
            Parameters parameters) {
        return ResponseDefinitionBuilder.like(responseDefinition)
                .withHeader(HttpHeaders.CONNECTION, "close")
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
