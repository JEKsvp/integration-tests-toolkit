# Wiremock toolkit

## Introduction

The module provides enhanced tools to work with [Wiremock](http://wiremock.org/) in a Spring Boot environment.
[spring-cloud-contract-wiremock](http://cloud.spring.io/spring-cloud-contract/spring-cloud-contract.html#_spring_cloud_contract_wiremock)
is used as a wiremock server.

## Usage

### Simple request verification

To verify that a request was made to a Wiremock server, use the `WireMockVerifier` class:

```java
WireMockVerifier.verify(WireMockVerificationSpec.requestedFor(RequestMethod.GET, urlEqualTo("/test")));
```

### Number of requests verification

To verify that a request was made a certain number of times, use method:

```java
.withNumberOfInteractions(exactly(1))
```

### Request headers verification

To verify that request with specific headers was made, use method:

```java
.withHeader("Content-Type",equalTo("application/json"))
```

### Request body verification

To verify that request with specific body was made, use method:

```java
.withJsonBody("""
                {
                    "id": 1,
                    "name": "test"
                }
                """);

```

You can specify a custom comparator to change comparison rules and ignore some fields.

```java

.withJsonBody("""
                {
                    "key1": "val1",
                    "key2": "val2",
                    "key3": "val3"
                }
                """,
        JsonAssertUtils.withCompareRules(JSONCompareMode.STRICT, "key2","key3"))

```

Other types of body are supported as well.

```java
.withXmlBody(equalToXml(...));
```

```java
.withTextBody(equalTo(""));
```

and if you want to specify your custom body verifier, you can use method.

```java
.withBody(...);
```

### Waiting for asynchronous requests

If you use asynchronous communication, you can wait for a request to be made to Wiremock server.

```java
.waitAtMost(Duration.ofSeconds(5));
```

### Verification example

```java
void verifyRequest() {
    WireMockVerifier.verify(WireMockVerificationSpec.requestedFor(RequestMethod.POST, urlEqualTo("/test"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withJsonBody("""
                            {
                                "id": 1,
                                "name": "test"
                            }
                            """,
                    JsonAssertUtils.withCompareRules(JSONCompareMode.STRICT, "id"))
            .withNumberOfInteractions(exactly(1))
            .waitAtMost(Duration.ofSeconds(5))
    );
}
```