/**
 * Provides Micronaut Serialization for {@link com.leakyabstractions.result.api.Result Result} objects.
 * <p>
 * <img src="https://dev.leakyabstractions.com/result-api/result.svg" alt="Result Library">
 * <h2>Micronaut Serialization for Result</h2>
 * <p>
 * When using {@link com.leakyabstractions.result.api.Result Result} objects with
 * <a href="https://micronaut.io">Micronaut</a>, we might run into some problems. The
 * <a href="https://micronaut-projects.github.io/micronaut-serialization/latest/guide/">Micronaut serialization</a>
 * support for Result solves them by making Micronaut treat results as {@link io.micronaut.serde.annotation.Serdeable
 * Serdeable} (so they can be serialized and deserialized).
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 * @see <a href="https://result.leakyabstractions.com/add-ons/micronaut">Quick guide</a>
 * @see <a href="https://leanpub.com/result/">Free book</a>
 * @see <a href="https://github.com/LeakyAbstractions/result-micronaut-serde/">Source code</a>
 * @see com.leakyabstractions.result.micronaut.serde.ResultArgument
 * @see com.leakyabstractions.result.api.Result Result
 */

package com.leakyabstractions.result.micronaut.serde;
