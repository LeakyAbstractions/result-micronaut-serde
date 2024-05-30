package example;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.leakyabstractions.result.core.Results.failure;
import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ApiController")
@MicronautTest
class ApiControllerTest {

    @Test
    void testGetLastOperation(@Client("/") HttpClient httpClient) {
        // Given
        Argument<ApiOperation> argument = Argument.of(ApiOperation.class);
        HttpRequest<Object> request = GET("/operations/last");
        // When
        ApiOperation operation = httpClient.toBlocking().retrieve(request, argument);
        // Then
        assertEquals("setup", operation.name());
        assertEquals("Perfect", operation.result().orElse(null));
    }

    @Test
    void testNotify(@Client("/") HttpClient httpClient) {
        // Given
        ApiOperation operation = new ApiOperation("pop", failure("empty stack"));
        Argument<Map<String, String>> argument = Argument.mapOf(String.class, String.class);
        HttpRequest<ApiOperation> request = POST("/operations/notify", operation);
        // When
        Map<String, String> map = httpClient.toBlocking().retrieve(request, argument);
        // Then
        assertEquals("pop failed: empty stack", map.get("error"));
    }
}
