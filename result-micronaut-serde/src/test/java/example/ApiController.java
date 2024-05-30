/** {% if false %} */

package example;

import com.leakyabstractions.result.core.Results;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import java.util.Map;

@SuppressWarnings("java:S125")
/** {% endif %} API controller */
@Controller("/operations")
public class ApiController {

    /** {% if include.fragment == "get_endpoint" %} Get last operation */
    @Get("/last")
    ApiOperation lastOperation() {
        return new ApiOperation("setup", Results.success("Perfect"));
    }
    // This endpoint returns a successful operation{% endif %}{% if include.fragment == "post_endpoint" %} Notify operation */
    @Post("/notify")
    Map<String, String> notify(@Body ApiOperation op) {
        return op.result()
                .mapSuccess(s -> Map.of("message", op.name() + " succeeded: " + s))
                .orElseMap(f -> Map.of("error", op.name() + " failed: " + f));
    }
    // This endpoint returns a simple map for the sake of simplicity{% endif %}
}