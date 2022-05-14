package stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {
    private static final String ORDER_PATH = "/api/orders";

    @Step("Create an order. With authorization")
    public ValidatableResponse createOrderWithAuth(Order order, String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Create an order. Without authorization")
    public ValidatableResponse createOrderWithoutAuth(Order order){
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Get a list of orders. With Authorization")
    public Response getUsersOrdersWithAuth(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH);
    }

    @Step("Get a list of orders. Without authorization")
    public Response getUsersOrdersWithoutAuth(){
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH);
    }


}
