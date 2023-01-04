package tests;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import lib.ApiCoreRequests;
import lib.DataGenerator;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

@Epic("Users")
@Feature("User deletion")
public class UserDeleteTest extends BaseTestCase{
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Description("This test check user delete HW18")
    @DisplayName("Test positive delete of user")
    @Test
    @Issue(value = "Ex18")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.CRITICAL)

    public void testDeleteUser() {
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        JsonPath jsonCreateAuth = responseCreateAuth.jsonPath();
        String userId = jsonCreateAuth.getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //delete
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, header,cookie);

        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/"  + userId, this.getHeader(responseGetAuth, "x-csrf-token"),this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }
    @Description("This test check updating of user with wrong email HW18")
    @DisplayName("Test negative updating of user")
    @Test
    @Issue(value = "Ex18")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.MINOR)

    public void testEditWithWrongEmail() {
        //Login
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData);
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //delete
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2", header, cookie);


        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Description("This test check user deletion with other user HW18")
    @DisplayName("Test negative delete of user")
    @Test
    @Issue(value = "Ex18")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.MINOR)

    public void testDeleteUserWithAnother() {
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        JsonPath jsonCreateAuth = responseCreateAuth.jsonPath();
        String userId = jsonCreateAuth.getString("id");

        //Login with other
        Map<String, String> userData2 = new HashMap<>();
        userData2.put("email", "vinkotov@example.com");
        userData2.put("password", "1234");
        Response responseAuth2 = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData2);
        String header2 = this.getHeader(responseAuth2,"x-csrf-token");
        String cookie2 = this.getCookie(responseAuth2, "auth_sid");

        //delete
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, header2, cookie2);

        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }
}
