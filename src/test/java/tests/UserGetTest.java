package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Getting Information cases")
@Feature("Get Information")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Description("Trying to get information about not authorized user by other user HW EX16")
    @DisplayName("Information about not authorized user")
    @Test
    public void testGetUserDetailsAuthAsOtherUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response resp = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData );

        String cookie = this.getCookie(resp,"auth_sid");
        String header = this.getHeader(resp,"x-csrf-token");

        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/1", header, cookie );

        String[] unexpectedFields = {"firstName", "lastName","email"};
        responseUserData.prettyPrint();
        Assertions.assertJsonHasNoFields(responseUserData, unexpectedFields);
    }
    @Description("Try to get information with not authorized user")
    @DisplayName("testGetUserDataNotAuth")
    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNoField(responseUserData, "firstName");
        Assertions.assertJsonHasNoField(responseUserData, "lastName");
        Assertions.assertJsonHasNoField(responseUserData, "email");
    }
    @Description("Try to get information about authorized other user")
    @DisplayName("testGetUserDetailsAuthAsSomeUser")
    @Test
    public void testGetUserDetailsAuthAsSomeUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response resp = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String cookie = this.getCookie(resp,"auth_sid");
        String header = this.getHeader(resp,"x-csrf-token");
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();
        String[] expectedFields = {"username", "firstName", "lastName","email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }
}
