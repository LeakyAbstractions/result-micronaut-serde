---
title: Micronaut Serialization for Result
description: Result-Micronaut-Serde provides Micronaut serialization support for Result objects
image: https://dev.leakyabstractions.com/result/result-magic-ball.png
---

# Micronaut Serialization for Result

This library provides [Micronaut serialization support][MICRONAUT_SERDE] for [Results objects][RESULT].


## Introduction

When using [<tt>Result</tt> objects][RESULT_REPO] with [Micronaut][MICRONAUT], we might run into some problems.
[This library][RESULT_MICRONAUT_SERDE_REPO] solves them by making Micronaut treat results as *Serdeable* so that they
can be serialized and deserialized.

Let's start by creating a record `ApiOperation` containing one ordinary and one <tt>Result</tt> field:

```java
{% include_relative result-micronaut-serde/src/test/java/example/ApiOperation.java %}
```


## Problem Overview

We will take a look at what happens when we try to serialize and deserialize <tt>ApiOperation</tt> objects with
Micronaut.

First, let's make sure we're using the latest versions of both the [Micronaut Framework][MICRONAUT_LAUNCH] and the
[Result library][RESULT_LATEST].


### Serialization Problem

Now, let's create a Micronaut controller that returns an instance of `ApiOperation` containing a successful result:

```java
{% include_relative result-micronaut-serde/src/test/java/example/ApiController.java fragment="get_endpoint" %}
```

And finally, let's run the application and try the `/operations/last` endpoint we just created:

```bash
curl 'http://localhost:8080/operations/last'
```

We'll see that we get a Micronaut `CodecException` caused by a `SerdeException`:

```
{% include_relative result-micronaut-serde/src/test/resources/serialization_error.txt %}
```

Although this may look strange, it's actually what we should expect. Even though we annotated `ApiOperation` as
`@Serdeable`, Micronaut doesn't know how to serialize result objects yet, so the data structure cannot be serialized.

```java
{% include_relative result-micronaut-serde/src/test/java/example/ProblemTest.java test="serialization_problem" %}
```

This is Micronaut's default serialization behavior. But we'd like to serialize the `result` field like this:

```json
{% include_relative result-micronaut-serde/src/test/resources/expected_serialized_result.json %}
```


### Deserialization Problem

Now, let's reverse our previous example, this time trying to receive an <tt>ApiOperation</tt> as the body of a `POST`
request:

```java
{% include_relative result-micronaut-serde/src/test/java/example/ApiController.java fragment="post_endpoint" %}
```

We'll see that now we get an `InvalidDefinitionException`. Let's view the stack trace:

```
{% include_relative result-micronaut-serde/src/test/resources/deserialization_error.txt %}
```

This behavior again makes sense. Essentially, Micronaut doesn't have a clue how to create new <tt>Result</tt> objects,
because <tt>Result</tt> is not annotated as `@Introspected` or `@Serdeable`.

```java
{% include_relative result-micronaut-serde/src/test/java/example/ProblemTest.java test="deserialization_problem" %}
```


## Solution

What we want, is for Micronaut to treat <tt>Result</tt> values as JSON objects that contain either a `success` or a
`failure` value. Fortunately, this problem has been solved for us. [This library][RESULT_MICRONAUT_SERDE_REPO] provides
serialization / deserialization support for <tt>Result</tt> objects.

All we need to do now is add the latest version as a Maven dependency:

- groupId: `com.leakyabstractions`
- artifactId: `result-micronaut-serde`
- version: `{{ site.current_version }}`

Once the library is in the classpath, all functionality is available for all normal Micronaut operations.


### Serialization Solution

Now, let's try and serialize our `ApiOperation` object again:

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="serialization_solution_successful_result" %}
```

If we look at the serialized response, we'll see that this time the `result` field contains a `success` field:

```json
{% include_relative result-micronaut-serde/src/test/resources/serialization_solution_successful_result.json %}
```

Next, we can try serializing a failed result:

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="serialization_solution_failed_result" %}
```

And we can verify that the serialized response contains a non-null `failure` value and a null `success` value:

```json
{% include_relative result-micronaut-serde/src/test/resources/serialization_solution_failed_result.json %}
```


### Deserialization Solution

Now, let's repeat our tests for deserialization. If we read our `ApiOperation` again, we'll see that we no longer get an
`InvalidDefinitionException`:

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="deserialization_solution_successful_result" %}
```

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an
exception, and in fact, have a failed <tt>Result</tt>:

```java
{% include_relative result-micronaut-serde/src/test/java/example/SolutionTest.java test="deserialization_solution_failed_result" %}
```


## Conclusion

We've shown how to use [Result][RESULT] with [Micronaut][MICRONAUT] without any problems by leveraging the
[Micronaut serialization support for Results][RESULT_MICRONAUT_SERDE_REPO],
demonstrating how it enables Micronaut to treat <tt>Result</tt> objects as ordinary fields.

The implementation of these examples can be found [here][EXAMPLE].


[EXAMPLE]:                      https://github.com/LeakyAbstractions/result-micronaut-serde/blob/main/result-micronaut-serde/src/test/java/example/SolutionTest.java
[MICRONAUT_LAUNCH]:             https://micronaut.io/launch
[MICRONAUT]:                    https://micronaut.io/
[MICRONAUT_SERDE]:              https://micronaut-projects.github.io/micronaut-serialization/latest/guide/
[RESULT]:                       https://dev.leakyabstractions.com/result/
[RESULT_MICRONAUT_SERDE_REPO]:  https://github.com/LeakyAbstractions/result-micronaut-serde/
[RESULT_LATEST]:                https://search.maven.org/artifact/com.leakyabstractions/result/
[RESULT_REPO]:                  https://github.com/LeakyAbstractions/result/
