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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.leakyabstractions.result.api.Result;

import io.micronaut.core.type.Argument;

/**
 * Tests for {@link ResultArgument}.
 *
 * @author Guillermo Calvo
 */
@DisplayName("ResultArgument")
class ResultArgumentTest {

    @Test
    void testSimpleScenario() {
        // Given
        final Argument<Result<String, Integer>> argument = resultOf(String.class, Integer.class);
        // Expect
        assertThat(argument).hasFieldOrPropertyWithValue("type", Result.class);
        assertThat(argument.hasTypeVariables()).isTrue();
        assertThat(argument.getTypeParameters()).hasSize(2);
        assertThat(argument.getTypeVariable(SUCCESS_NAME))
                .hasValueSatisfying(s -> assertThat(s).hasFieldOrPropertyWithValue("type", String.class));
        assertThat(argument.getTypeVariable(FAILURE_NAME))
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("type", Integer.class));
    }

    @Test
    void testComplexScenario() {
        // Given
        final String name = "T";
        final Argument<String> successArgument = successOf(String.class);
        final Argument<Integer> failureArgument = failureOf(Integer.class);
        final Argument<Result<String, Integer>> argument = resultOf(name, successArgument, failureArgument);
        // Expect
        assertThat(argument)
                .hasFieldOrPropertyWithValue("type", Result.class)
                .hasFieldOrPropertyWithValue("name", name);
        assertThat(argument.hasTypeVariables()).isTrue();
        assertThat(argument.getTypeParameters()).hasSize(2);
        assertThat(argument.getTypeVariable(SUCCESS_NAME))
                .hasValueSatisfying(s -> assertThat(s).hasFieldOrPropertyWithValue("type", String.class));
        assertThat(argument.getTypeVariable(FAILURE_NAME))
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("type", Integer.class));
    }

    @Test
    void testNestedScenario() {
        // Given
        final String name = "T";
        @SuppressWarnings("unchecked")
        final Argument<Map<String, Object>> successArgument = successOf(
                (Class<Map<String, Object>>) (Class<?>) Map.class,
                Argument.ofTypeVariable(String.class, "K"),
                Argument.ofTypeVariable(Object.class, "V"));
        @SuppressWarnings("unchecked")
        final Argument<List<Integer>> failureArgument = failureOf(
                (Class<List<Integer>>) (Class<?>) List.class,
                Argument.ofTypeVariable(Integer.class, "E"));
        final Argument<Result<Map<String, Object>, List<Integer>>> argument = resultOf(name, successArgument,
                failureArgument);
        // Expect
        assertThat(argument)
                .hasFieldOrPropertyWithValue("type", Result.class)
                .hasFieldOrPropertyWithValue("name", name);
        assertThat(argument.hasTypeVariables()).isTrue();
        assertThat(argument.getTypeParameters()).hasSize(2);
        assertThat(argument.getTypeVariable(SUCCESS_NAME))
                .hasValueSatisfying(
                        s -> {
                            assertThat(s).hasFieldOrPropertyWithValue("type", Map.class);
                            assertThat(s.hasTypeVariables()).isTrue();
                            assertThat(s.getTypeParameters()).hasSize(2);
                            assertThat(s.getTypeVariable("K"))
                                    .hasValueSatisfying(
                                            k -> assertThat(k).hasFieldOrPropertyWithValue("type", String.class));
                            assertThat(s.getTypeVariable("V"))
                                    .hasValueSatisfying(
                                            v -> assertThat(v).hasFieldOrPropertyWithValue("type", Object.class));
                        });
        assertThat(argument.getTypeVariable(FAILURE_NAME))
                .hasValueSatisfying(
                        f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("type", List.class);
                            assertThat(f.hasTypeVariables()).isTrue();
                            assertThat(f.getTypeParameters()).hasSize(1);
                            assertThat(f.getTypeVariable("E"))
                                    .hasValueSatisfying(
                                            k -> assertThat(k).hasFieldOrPropertyWithValue("type", Integer.class));
                        });
    }
}
