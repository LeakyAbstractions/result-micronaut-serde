---
title: Micronaut Serialization for Result
description: Result-Micronaut-Serde provides Micronaut serialization support for Result objects
image: https://dev.leakyabstractions.com/result/result-banner.png
---

# Micronaut Serialization for Result

When using Result objects with [Micronaut][MICRONAUT], we might run into some problems. The
[Micronaut serialization][MICRONAUT_SERDE] support for Result solves them by making Micronaut treat results as
[`Serdeable`][SERDEABLE] (so they can be serialized and deserialized).

> [Micronaut][MICRONAUT] is a modern, JVM-based framework for building lightweight microservices and serverless
> applications. It focuses on fast startup times and low memory usage. Although not as widely adopted as
> [Spring Boot][SPRING_BOOT], it has gained popularity for its performance and innovative features.


## How to Use this Add-On

Add this Maven dependency to your build:

* **Group ID**: `com.leakyabstractions`
* **Artifact ID**: `result-micronaut-serde`
* **Version**: `{{ site.current_version }}`

[Maven Central][ARTIFACTS] provides snippets for different build tools to declare this dependency.


## Test Scenario

Let's start by creating a record `ApiOperation` containing one ordinary and one Result field.

```java
{% include_relative result-micronaut-serde/src/test/java/example/ApiOperation.java %}
```


## Problem Overview

We will take a look at what happens when we try to serialize and deserialize `ApiOperation` objects with Micronaut.


### Serialization Problem

Now, let's create a Micronaut controller that returns an instance of `ApiOperation` containing a successful result.

```java
{% include_relative result-micronaut-serde/src/test/java/example/ApiController.java fragment="get_endpoint" %}
```

And finally, let's run the application and try the `/operations/last` endpoint we just created.

```bash
curl 'http://localhost:8080/operations/last'
```

We'll see that we get a Micronaut `CodecException` caused by a [`SerdeException`][SERDE_EXCEPTION].

```
{% include_relative result-micronaut-serde/src/test/resources/serialization_error.txt %}
```

Although this may look strange, it's actually what we should expect. Even though we annotated `ApiOperation` as
[`@Serdeable`][SERDEABLE], Micronaut doesn't know how to serialize result objects yet, so the data structure cannot be
serialized.

```java
{% include_relative result-micronaut-serde/src/test/java/example/ProblemTest.java test="serialization_problem" %}
```

This is Micronaut's default serialization behavior. But we'd like to serialize the `result` field like this:

```json
{% include_relative result-micronaut-serde/src/test/resources/expected_serialized_result.json %}
```


### Deserialization Problem

Now, let's reverse our previous example, this time trying to receive an `ApiOperation` as the body of a `POST` request.

```java
{% include_relative result-micronaut-serde/src/test/java/example/ApiController.java fragment="post_endpoint" %}
```

We'll see that now we get an [`IntrospectionException`][INTROSPECTION_EXCEPTION]. Let's inspect the stack trace.

```
{% include_relative result-micronaut-serde/src/test/resources/deserialization_error.txt %}
```

This behavior again makes sense. Essentially, Micronaut cannot create new result objects, because `Result` is not
annotated as [`@Introspected`][INTROSPECTED] or [`@Serdeable`][SERDEABLE].

```java
{% include_relative result-micronaut-serde/src/test/java/example/ProblemTest.java test="deserialization_problem" %}
```


## Solution Implementation

What we want, is for Micronaut to treat Result values as JSON objects that contain either a `success` or a `failure`
value. Fortunately, there's an easy way to solve this problem.


### Adding the Serde Imports to the Classpath

All we need to do now is add Result-Micronaut-Serde as a Maven dependency. Once the [`@SerdeImport`][SERDE_IMPORT] is in
the classpath, all functionality is available for all normal Micronaut operations.


## Serializing Results

Now, let's try and serialize our `ApiOperation` object again.

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="serialize_successful_result" %}
```

If we look at the serialized response, we'll see that this time the `result` field contains a `success` field.

```json
{% include_relative result-micronaut-serde/src/test/resources/serialization_solution_successful_result.json %}
```

Next, we can try serializing a failed result.

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="serialize_failed_result" %}
```

We can verify that the serialized response contains a non-null `failure` value and a null `success` value:

```json
{% include_relative result-micronaut-serde/src/test/resources/serialization_solution_failed_result.json %}
```


## Deserializing Results

Now, let's repeat our tests for deserialization. If we read our `ApiOperation` again, we'll see that we no longer get an
[`IntrospectionException`][INTROSPECTION_EXCEPTION].

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="deserialize_successful_result" %}
```

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an
exception, and in fact, have a failed result.

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="deserialize_failed_result" %}
```


## Conclusion

You have learned how to use results with [Micronaut][MICRONAUT] without any problems by leveraging the
[Micronaut serialization support for Result][RESULT_MICRONAUT_SERDE_REPO], demonstrating how it enables Micronaut to
treat Result objects as ordinary fields.

The full source code for the examples is [available on GitHub][SOURCE_CODE].


[ARTIFACTS]:                    https://central.sonatype.com/artifact/com.leakyabstractions/result-jackson/
[INTROSPECTED]:                 https://javadoc.io/doc/io.micronaut/micronaut-core/latest/io/micronaut/core/annotation/Introspected.html
[INTROSPECTION_EXCEPTION]:      https://javadoc.io/doc/io.micronaut/micronaut-core/latest/io/micronaut/core/beans/exceptions/IntrospectionException.html
[MICRONAUT]:                    https://micronaut.io/
[MICRONAUT_SERDE]:              https://micronaut-projects.github.io/micronaut-serialization/latest/guide/
[RESULT]:                       https://result.leakyabstractions.com/
[RESULT_LATEST]:                https://search.maven.org/artifact/com.leakyabstractions/result/
[RESULT_MICRONAUT_SERDE_REPO]:  https://github.com/LeakyAbstractions/result-micronaut-serde/
[RESULT_REPO]:                  https://github.com/LeakyAbstractions/result/
[SERDEABLE]:                    https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/annotation/Serdeable.html
[SERDE_EXCEPTION]:              https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/exceptions/SerdeException.html
[SERDE_IMPORT]:                 https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/annotation/SerdeImport.html
[SOURCE_CODE]:                  https://github.com/LeakyAbstractions/result-micronaut-serde/tree/main/result-micronaut-serde/src/test/java/example
[SPRING_BOOT]:                  https://spring.io/projects/spring-boot
