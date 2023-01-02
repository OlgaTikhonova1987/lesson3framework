package tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lib.ApiCoreRequests;
import io.qameta.allure.Description;
import lib.BaseTestCase;
import lib.Assertions;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;
import lib.DataGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
@Epic("User creation cases")
@Feature("Creation")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check creating of user with email without @")
    @DisplayName("Test negative creating of user")
    @Test
    public void testCreateUserWithWrongEmail() {
        String email = DataGenerator.getRandomEmailWithoutAt();
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }
    @Description("This test check creating of user without one of the fields")
    @DisplayName("Test negative creating of user")
    @ParameterizedTest
    @ValueSource(strings =  {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutAnyField(String data) {

        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.put(data, null);
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: "+ data);
    }
    @Description("This test check creating of user with short name")
    @DisplayName("Test negative creating of user")
    @Test
    public void testCreateUserWithShortName() {
        String firstname = DataGenerator.getRandomValueWithSpecialLength(1);

        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstname);
        userData = DataGenerator.getRegistrationData(userData);
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too short");
    }
    @Description("This test check creating of user with long name")
    @DisplayName("Test negative creating of user")
    @Test
    public void testCreateUserWithLongName() {
        String firstname = DataGenerator.getRandomValueWithSpecialLength(251);

        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstname);
        userData = DataGenerator.getRegistrationData(userData);
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too long");
    }
    @Description("This test check an error while user with existing email created")
    @DisplayName("Test negative creating of user")
    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '"+ email + "' already exists");
    }
    @Description("This test check positive user creating")
    @DisplayName("Test positive creating of user")
    @Test
    public void testCreateUserSuccessfully() {
      Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData );
        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }


}
