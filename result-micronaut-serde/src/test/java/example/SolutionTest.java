/**{% if false %}*/

package example;

import static com.leakyabstractions.result.core.Results.success;
import static com.leakyabstractions.result.core.Results.failure;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("Solution")
@MicronautTest(startApplication = false)
@SuppressWarnings("java:S125")
class SolutionTest {

/** {% elsif include.test == "serialize_successful_result" %} Test serialization solution with a successful result */
@Test
void serializeSuccessfulResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  ApiOperation op = new ApiOperation("clean", success("All good"));
  // When
  String json = objectMapper.writeValueAsString(op);
  // Then
  assertEquals("""
      {"name":"clean","result":{"success":"All good"}}""", json);
} // End{% endif %}{% if false %}

/** {% elsif include.test == "serialize_failed_result" %} Test serialization problem with a failed result */
@Test
void serializeFailedResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  ApiOperation op = new ApiOperation("build", failure("Oops"));
  // When
  String json = objectMapper.writeValueAsString(op);
  // Then
  assertEquals("""
      {"name":"build","result":{"failure":"Oops"}}""", json);
} // End{% endif %}{% if false %}

/** {% elsif include.test == "deserialize_successful_result" %} Test deserialization solution with a successful result */
@Test
void deserializeSuccessfulResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  String json = """
      {"name":"check","result":{"success":"Yay"}}""";
  // When
  ApiOperation response = objectMapper.readValue(json, ApiOperation.class);
  // Then
  assertEquals("check", response.name());
  assertEquals("Yay", response.result().orElse(null));
} // End{% endif %}{% if false %}

/** {% elsif include.test == "deserialize_failed_result" %} Test deserialization solution with a failed result */
@Test
void deserializeFailedResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  String json = """
      {"name":"start","result":{"failure":"Nay"}}""";
  // When
  ApiOperation response = objectMapper.readValue(json, ApiOperation.class);
  // Then
  assertEquals("start", response.name());
  assertEquals("Nay", response.result().getFailure().orElse(null));
} // End{% endif %}{% if false %}

}
// {% endif %}