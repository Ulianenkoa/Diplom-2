package stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetOrdersListTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private User user;
    String accessToken;

    @Before
    public void setUp(){
        userClient = new UserClient();
        user = User.getRandomUser();
        userClient.createUser(user);
        orderClient = new OrderClient();

    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Getting a list of orders by an authorized user")
    public void getOrderListForAuthorizedUser(){
        UserCredentials credentials= UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);
        accessToken = loginResponse.extract().path("accessToken");

        Response orderResponse = orderClient.getUsersOrdersWithAuth(accessToken);

        int statusCode = orderResponse.statusCode();
        boolean success = orderResponse.path("success");
        String total = orderResponse.path("total");

        assertThat("User has created", statusCode, equalTo(SC_OK));
        assertThat("State if courier has created", success, equalTo(true));
        assertThat("State if courier has created", total, notNullValue());
    }

    @Test
    @DisplayName("Getting a list of orders by an unauthorized user")
    public void getOrderListForUnauthorizedUser(){
        Response orderResponse = orderClient.getUsersOrdersWithoutAuth();

        int statusCode = orderResponse.statusCode();
        boolean success = orderResponse.path("success");
        String message = orderResponse.path("message");

        assertThat("User has created", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("State if courier has created", success, equalTo(false));
        assertThat("State if courier has created", message, equalTo("You should be authorised"));
    }
}
