package lib;
import io.restassured.response.Response;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

public class Assertions {
    public static void  assertJsonByName(Response Response, String name, int expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));
        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }
    public static void  assertJsonByName(Response Response, String name, String expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));
        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode, Response.statusCode(),
                "Response status Code is not as expected"
        );
    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer) {
        assertEquals(
                expectedAnswer, Response.asString(),
                "Response text is not as expected"
        );

    }
    public static void assertJsonHasField(Response Response, String expectedFildIName) {
        Response.then().assertThat().body("$", hasKey(expectedFildIName));

    }
    public static void assertJsonHasNoField(Response Response, String unexpectedFildIName) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedFildIName)));

    }
    public static void assertJsonHasFields(Response Response, String[] expectedFieldINames) {
        for (String expectedFieldName : expectedFieldINames) {
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }

    }
    public static void assertJsonHasNoFields(Response Response, String[] unexpectedFildINames) {
        for (String unexpectedFieldName : unexpectedFildINames) {
            Assertions.assertJsonHasNoField(Response, unexpectedFieldName);
        }

    }

}
