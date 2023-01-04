package tests

import io.qameta.allure.Issue
import io.qameta.allure.Owner
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel

import static org.junit.jupiter.api.Assertions.assertEquals;
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.restassured.path.json.JsonPath
import lib.ApiCoreRequests
import lib.DataGenerator
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Users")
@Feature("User Update")
class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check updating of user")
    @DisplayName("Test positive updating of user")
    @Test
    @Issue(value = "Ex17")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.CRITICAL)
    public void testEditJustCreatedTest() {
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        responseCreateAuth.prettyPrint();
        String userId = responseCreateAuth.getString("id");
        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //Get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
        responseUserData.prettyPrint();
        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }
    @Description("This test check updating of user without authorising HW17")
    @DisplayName("Test negative updating of user")
    @Test
    @Issue(value = "Ex17")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.NORMAL)
    public void testEditUnAuthorised() {

        //edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/1", editData);

        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }
    @Description("This test check updating of user with other user HW17")
    @DisplayName("Test negative updating of user")
    @Test
    @Issue(value = "Ex17")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.NORMAL)
    public void testEditWithOtherUser() {
        //Login
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData)

        //edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/14", this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),  editData);
        Assertions.assertResponseTextEquals(responseEditUser, "Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
    }
    @Description("This test check updating of user with wrong email HW17")
    @DisplayName("Test negative updating of user")
    @Test
    @Issue(value = "Ex17")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.NORMAL)
    public void testEditWithWrongEmail() {
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        JsonPath jsonCreateAuth = responseCreateAuth.jsonPath();
        String userId = jsonCreateAuth.getString("id");
        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",authData)

        //edit
        Map<String, String> editData = new HashMap<>();
        editData.put("email", DataGenerator.getRandomEmailWithoutAt());
        Response responseEditUser = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/"+userId, this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),  editData);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");

    }
    @Description("This test check updating of user with short firstname HW17")
    @DisplayName("Test negative updating of user")
    @Test
    @Issue(value = "Ex17")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.NORMAL)
    public void testEditWithShortFirstname() {
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        JsonPath jsonCreateAuth = responseCreateAuth.jsonPath();
        String userId = jsonCreateAuth.getString("id");
        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",authData)

        //edit
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", DataGenerator.getRandomValueWithSpecialLength(1));
        Response responseEditUser = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/"+userId, this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),  editData);
        JsonPath jsonResponseEditUser = responseEditUser.jsonPath();
        String resp = jsonResponseEditUser.getString("error");
        assertEquals( "Too short value for field firstName", resp);

    }
}
