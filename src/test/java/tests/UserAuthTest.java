package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

@Epic("Users")
@Feature("User Authorisation")
public class UserAuthTest extends BaseTestCase  {
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @BeforeEach
    public void loginUser()
    {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response resp = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData );

        this.cookie = this.getCookie(resp,"auth_sid");
        this.header = this.getHeader(resp,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(resp, "user_id");

    }
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    @Test
    @Issue(value = "Lec3")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.BLOCKER)

    public void testAuthUser() {

        Response respCh = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.header, this.cookie);

        Assertions.assertJsonByName(respCh, "user_id", this.userIdOnAuth);


    }
    @Description("This Test checks auth status w/o sending auth cookie or token")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    @Issue(value = "Lec3")
    @Owner(value ="Tikhonova")
    @Severity(value = SeverityLevel.NORMAL)
    public void testNegativeAuthUser(String condition) {
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie("https://playground.learnqa.ru/api/user/auth", this.cookie );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken("https://playground.learnqa.ru/api/user/auth", this.header );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        }else {
            throw new IllegalArgumentException("Condition value is known: "+ condition);
        }
    }
}
