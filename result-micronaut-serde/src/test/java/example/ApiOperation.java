/** Represents an API operation{% if false %} */

package example;

import com.leakyabstractions.result.api.Result;
import io.micronaut.serde.annotation.Serdeable;

/** {% endif %} */
@Serdeable
public record ApiOperation(String name, Result<String, String> result) {
}