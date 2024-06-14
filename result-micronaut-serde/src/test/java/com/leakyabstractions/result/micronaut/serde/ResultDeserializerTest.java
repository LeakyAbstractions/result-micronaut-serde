/*
 * Copyright 2024 Guillermo Calvo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leakyabstractions.result.micronaut.serde;

import static com.leakyabstractions.result.micronaut.serde.ResultArgument.FAILURE_NAME;
import static com.leakyabstractions.result.micronaut.serde.ResultArgument.SUCCESS_NAME;
import static com.leakyabstractions.result.micronaut.serde.ResultArgument.failureOf;
import static com.leakyabstractions.result.micronaut.serde.ResultArgument.resultOf;
import static com.leakyabstractions.result.micronaut.serde.ResultArgument.successOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;

import io.micronaut.core.type.Argument;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Tests for {@link ResultDeserializer}.
 *
 * @author Guillermo Calvo
 */
@DisplayName("ResultDeserializer")
@MicronautTest(startApplication = false)
class ResultDeserializerTest {

    @Inject
    ObjectMapper mapper;

    @Serdeable.Deserializable
    record Foobar<T>(T foo, Boolean bar) {
    }

    @Test
    void read_successful_result() throws IOException {
        // Given
        final String json = "{\"success\":\"SUCCESS\"}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, resultOf(String.class, Integer.class));
        // Then
        assertThat(result).extracting("success", OPTIONAL).contains("SUCCESS");
    }

    @Test
    void read_successful_map_result() throws IOException {
        // Given
        final String json = "{\"success\":{\"x\":\"SUCCESS\",\"y\":123}}";
        // When
        final Result<?, ?> result = mapper.readValue(json, Result.class);
        // Then
        assertThat(result)
                .extracting("success", OPTIONAL)
                .hasValueSatisfying(
                        x -> {
                            assertThat(x).hasFieldOrPropertyWithValue("x", "SUCCESS");
                            assertThat(x).hasFieldOrPropertyWithValue("y", 123);
                        });
    }

    @Test
    void read_failed_result() throws IOException {
        // Given
        final String json = "{\"failure\":404}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, resultOf(String.class, Integer.class));
        // Then
        assertThat(result).extracting("failure", OPTIONAL).contains(404);
    }

    @Test
    void read_null() throws IOException {
        // Given
        final String json = "null";
        // When
        final Result<?, ?> result = mapper.readValue(json, Result.class);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_empty_result_as_null() throws IOException {
        // Given
        final String json = "{}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, resultOf(String.class, Integer.class));
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_malformed_result_as_failure() throws IOException {
        // Given
        final String json = "{\"success\":123,\"failure\":321}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, resultOf(String.class, Integer.class));
        // Then
        assertThat(result).extracting("failure", OPTIONAL).contains(321);
    }

    @Test
    void write_successful_result() throws IOException {
        // Given
        final Result<?, ?> result = Results.success("OK");
        // When
        final String json = mapper.writeValueAsString(result);
        // Then
        assertThat(json).isEqualTo("{\"success\":\"OK\"}");
    }

    @Test
    void write_failed_result() throws IOException {
        // Given
        final Result<?, ?> result = Results.failure("FAILURE");
        // When
        final String json = mapper.writeValueAsString(result);
        // Then
        assertThat(json).isEqualTo("{\"failure\":\"FAILURE\"}");
    }

    @Test
    void read_complex_successful_result() throws IOException {
        // Given
        final String json = "{\"success\":{\"foo\":[1,2,3],\"bar\":true}}";
        final Argument<Result<Foobar<List<Integer>>, Object>> arg = resultOf(
                foobarOf(SUCCESS_NAME, Argument.listOf(Integer.class)), failureOf(Object.class));
        // When
        final Result<Foobar<List<Integer>>, ?> result = mapper.readValue(json, arg);
        // Then
        assertThat(result)
                .extracting("success", OPTIONAL)
                .hasValueSatisfying(
                        s -> assertThat(s)
                                .hasFieldOrPropertyWithValue("foo", List.of(1, 2, 3))
                                .hasFieldOrPropertyWithValue("bar", true));
    }

    @Test
    void read_complex_failed_result() throws IOException {
        // Given
        final String json = "{\"failure\":{\"foo\":404}}";
        final Argument<Result<Integer, Foobar<Long>>> arg = resultOf(successOf(Integer.class),
                foobarOf(FAILURE_NAME, Long.class));
        // When
        final Result<Integer, Foobar<Long>> result = mapper.readValue(json, arg);
        // Then
        assertThat(result)
                .extracting("failure", OPTIONAL)
                .hasValueSatisfying(
                        f -> assertThat(f)
                                .hasFieldOrPropertyWithValue("foo", 404L)
                                .hasFieldOrPropertyWithValue("bar", null));
    }

    @Test
    void read_complex_null() throws IOException {
        // Given
        final String json = "null";
        final Argument<Result<Foobar<Float>, String>> arg = resultOf(foobarOf(SUCCESS_NAME, Float.class),
                failureOf(String.class));
        // When
        final Result<?, ?> result = mapper.readValue(json, arg);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_complex_empty_result_as_null() throws IOException {
        // Given
        final String json = "{}";
        final Argument<Result<Foobar<String>, BigDecimal>> arg = resultOf(foobarOf(SUCCESS_NAME, String.class),
                failureOf(BigDecimal.class));
        // When
        final Result<Foobar<String>, BigDecimal> result = mapper.readValue(json, arg);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_malformed_complex_result_as_failure() throws IOException {
        // Given
        final String json = "{\"success\":{\"foo\":\"12.34\",\"bar\":true},\"failure\":321}";
        final Argument<Result<Foobar<BigDecimal>, Long>> arg = resultOf(foobarOf(SUCCESS_NAME, BigDecimal.class),
                failureOf(Long.class));
        // When
        final Result<Foobar<BigDecimal>, Long> result = mapper.readValue(json, arg);
        // Then
        assertThat(result).extracting("failure", OPTIONAL).contains(321L);
    }

    @SuppressWarnings("unchecked")
    static <T> Argument<Foobar<T>> foobarOf(String name, Argument<T> type) {
        return Argument.of((Class<Foobar<T>>) (Class<?>) Foobar.class, name, type);
    }

    static <T> Argument<Foobar<T>> foobarOf(String name, Class<T> clazz) {
        return foobarOf(name, Argument.of(clazz, "T"));
    }
}
