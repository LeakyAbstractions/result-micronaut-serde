import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.micronaut.serde.ResultArgument;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Provides Micronaut Serialization for {@link Result Result} objects.
 * <p>
 * <img src="https://dev.leakyabstractions.com/result-api/result.svg" alt="Result Library">
 * <h2>Micronaut Serialization for Result</h2>
 * <p>
 * When using {@link Result Result} objects with <a href="https://micronaut.io">Micronaut</a>, we might run into some
 * problems. The <a href="https://micronaut-projects.github.io/micronaut-serialization/latest/guide/">Micronaut
 * serialization</a> support for Result solves them by making Micronaut treat results as {@link Serdeable Serdeable} (so
 * they can be serialized and deserialized).
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 * @see <a href="https://result.leakyabstractions.com/add-ons/micronaut">Quick guide</a>
 * @see <a href="https://leanpub.com/result/">Free book</a>
 * @see <a href="https://github.com/LeakyAbstractions/result-micronaut-serde/">Source code</a>
 * @see ResultArgument
 * @see Result Result
 */
module com.leakyabstractions.result.micronaut.serde {
    exports com.leakyabstractions.result.micronaut.serde;

    requires transitive com.leakyabstractions.result.api;
    requires com.leakyabstractions.result.core;
    requires transitive io.micronaut.core;
    requires io.micronaut.serde.micronaut_serde_api;
    requires jakarta.inject;
}
