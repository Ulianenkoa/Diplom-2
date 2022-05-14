package stellarburgers;

import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {
    private static final String USER_PATH = "/api/auth/";

    @Step("Create user")
    public ValidatableResponse createUser(User user){
        String registerRequestBody = null;

        try {
            registerRequestBody = new ObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return given()
                .spec(getBaseSpec())
                .body(registerRequestBody)
                .when()
                .post(USER_PATH + "register")
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUser(UserCredentials credentials){

        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_PATH+"login")
                .then();
    }


    @Step("Update user data")
    public ValidatableResponse updateUserInfo(String accessToken,User user){
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .when()
                .delete(USER_PATH + "user")
                .then();
    }
}
