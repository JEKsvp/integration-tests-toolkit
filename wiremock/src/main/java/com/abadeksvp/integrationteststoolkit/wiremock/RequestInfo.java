package com.abadeksvp.integrationteststoolkit.wiremock;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class RequestInfo {
    private String url;
    private String body;
}
