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

import java.io.IOException;

import com.leakyabstractions.result.api.Result;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Deserializer;
import io.micronaut.serde.annotation.SerdeImport;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;

/**
 * Deserializes {@link Result} objects.
 *
 * @author Guillermo Calvo
 */
@SerdeImport(value = Result.class, mixin = ResultDeserializer.ResultMixin.class)
@Singleton
class ResultDeserializer implements Deserializer<Result<?, ?>> {

    @Serdeable.Deserializable(using = ResultDeserializer.class)
    interface ResultMixin {}

    @Override
    @Nullable
    public Result<?, ?> deserialize(
            @NonNull
            Decoder decoder,
            @NonNull
            DecoderContext context,
            @NonNull
            Argument<? super Result<?, ?>> type)
            throws IOException {
        @SuppressWarnings("unchecked")
        final Argument<ResultBuilder<?, ?>> builderType = (Argument<ResultBuilder<?, ?>>) (Argument<?>) Argument
                .of(ResultBuilder.class, type.getTypeParameters());
        return context
                .findDeserializer(builderType)
                .createSpecific(context, builderType)
                .deserializeNullable(decoder, context, builderType)
                .build();
    }
}
