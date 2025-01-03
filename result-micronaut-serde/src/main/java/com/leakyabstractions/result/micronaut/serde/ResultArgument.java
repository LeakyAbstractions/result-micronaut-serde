/*
 * Copyright 2025 Guillermo Calvo
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

import static io.micronaut.core.annotation.AnnotationMetadata.EMPTY_METADATA;

import com.leakyabstractions.result.api.Result;

import io.micronaut.core.type.Argument;

/**
 * Creates arguments of {@link Result} type.
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 */
public class ResultArgument {

    /** The name of the generic success type. */
    public static final String SUCCESS_NAME = "S";

    /** The name of the generic failure type. */
    public static final String FAILURE_NAME = "F";

    private ResultArgument() {
        // Suppresses default constructor, ensuring non-instantiability
    }

    /**
     * Creates a new {@link Result} argument for the given success/failure arguments.
     *
     * @param name The argument name.
     * @param successType The success argument.
     * @param failureType The failure argument.
     * @param <S> The success type.
     * @param <F> The failure type.
     * @return An argument representing the {@link Result} type.
     */
    @SuppressWarnings("unchecked")
    public static <S, F> Argument<Result<S, F>> resultOf(
            String name, Argument<S> successType, Argument<F> failureType) {
        return Argument.of(
                (Class<Result<S, F>>) (Class<?>) Result.class, name, successType, failureType);
    }

    /**
     * Creates a new {@link Result} argument for the given success/failure arguments.
     *
     * @param successType The success argument.
     * @param failureType The failure argument.
     * @param <S> The success type.
     * @param <F> The failure type.
     * @return An argument representing the {@link Result} type.
     */
    public static <S, F> Argument<Result<S, F>> resultOf(
            Argument<S> successType, Argument<F> failureType) {
        return resultOf(null, successType, failureType);
    }

    /**
     * Creates a new {@link Result} argument for the given success/failure classes.
     *
     * @param successClass The success class.
     * @param failureClass The failure class.
     * @param <S> The success type.
     * @param <F> The failure type.
     * @return An argument representing the {@link Result} type.
     */
    public static <S, F> Argument<Result<S, F>> resultOf(
            Class<S> successClass, Class<F> failureClass) {
        return resultOf(successOf(successClass), failureOf(failureClass));
    }

    /**
     * Creates a new argument for the given generic success class.
     *
     * @param successClass The success class.
     * @param typeParameters The type parameters of the generic success type.
     * @param <S> The generic success type.
     * @return An argument representing the success type.
     */
    public static <S> Argument<S> successOf(Class<S> successClass, Argument<?>... typeParameters) {
        return Argument.ofTypeVariable(successClass, SUCCESS_NAME, EMPTY_METADATA, typeParameters);
    }

    /**
     * Creates a new argument for the given generic failure class.
     *
     * @param failureClass The failure class.
     * @param typeParameters The type parameters of the generic failure type.
     * @param <F> The generic failure type.
     * @return An argument representing the failure type.
     */
    public static <F> Argument<F> failureOf(Class<F> failureClass, Argument<?>... typeParameters) {
        return Argument.ofTypeVariable(failureClass, FAILURE_NAME, EMPTY_METADATA, typeParameters);
    }
}
