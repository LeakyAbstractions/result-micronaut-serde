/**{% if false %}*/

package example;

import static example.TestTypes.ApiOperation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.exceptions.SerdeException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Problem")
@MicronautTest(startApplication = false, rebuildContext = true)
@SuppressWarnings("java:S125")
class ProblemTest {

/** {% elsif include.test == "serialization_problem" %} Test serialization problem */
@Test
void serialization_problem(ObjectMapper objectMapper) {
  // Given
  ApiOperation op = new ApiOperation("setup", success("Perfect"));
  // Then
  SerdeException error = assertThrows(SerdeException.class,
      () -> objectMapper.writeValueAsString(op));
  assertTrue(error.getMessage().startsWith(
      "No serializable introspection present for type Success."));
} // End{% endif %}{% if false %}

@Test
void serialization_error_message(ObjectMapper objectMapper) throws Exception {
  // Given
  ApiOperation op = new ApiOperation("setup", success("Perfect"));
  String expected;
  try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
      "serialization_error.txt")))) {
    expected = br.lines().collect(Collectors.joining());
  }
  // Then
  SerdeException error = assertThrows(SerdeException.class,
      () -> objectMapper.writeValueAsString(op));
  assertEquals(expected, error.getMessage());
}

/** {% elsif include.test == "deserialization_problem" %} Test deserialization problem */
@Test
void deserialization_problem(ObjectMapper objectMapper) {
  // Given
  String json = """
      {"name":"renew","result":{"success":"OK"}}""";
  // Then
  IntrospectionException error = assertThrows(IntrospectionException.class,
      () -> objectMapper.readValue(json, ApiOperation.class));
  String errorMessage = error.getMessage(); // Extract error message{% endif %}{% if false %}
  errorMessage = replaceJavaPackage(errorMessage);
  // {% elsif include.test == "deserialization_problem" %}
  // Verify error message
  assertTrue(errorMessage.startsWith("No bean introspection available " +
      "for type [interface com.leakyabstractions.result.api.Result]."));
} // End{% endif %}{% if false %}

@Test
void deserialization_error_message(ObjectMapper objectMapper) throws Exception {
  // Given
  String json = """
      {"name":"renew","result":{"success":"OK"}}""";
  String expected;
  try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
      "deserialization_error.txt")))) {
    expected = br.lines().collect(Collectors.joining());
  }
  // Then
  IntrospectionException error = assertThrows(IntrospectionException.class,
    () -> objectMapper.readValue(json, ApiOperation.class));
  assertEquals(expected, replaceJavaPackage(error.getMessage()));
}

    static <S, F> TestTypes.Result<S, F> success(S success) {
        return new TestTypes.Success<>(success);
    }

    static String replaceJavaPackage(String errorMessage) {
        return errorMessage.replaceAll("example.TestTypes.", "com.leakyabstractions.result.api.");
    }
}

// We need result types with no introspection to reproduce the problems in absence of result-micronaut-serde.
interface TestTypes {
    interface Result<S, F> { }
    record Success<S, F>(S success) implements Result<S, F> { }
    @Serdeable
    record ApiOperation(String name, Result<String, String> result) { }
}
// {% endif %}