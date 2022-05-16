package stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private User user;
    String accessToken;

    @Before
    public void setUp() {
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
    @DisplayName("Creation of an order with valid data by an authorized user")
    public void createOrderWithAuthUser() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrderWithAuth(new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72")), accessToken);
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int orderNumber = orderResponse.extract().path("order.number");

        assertThat("User has created", statusCode, equalTo(SC_OK));
        assertThat("State if courier has created", success, equalTo(true));
        assertThat("State if courier has created", burgerName, notNullValue());
        assertThat("State if courier has created", orderNumber, notNullValue());
    }

    @Test
    @DisplayName("Creation of an order with valid data by an unauthorized user")
    public void createOrderWithoutAuthUser() {

        ValidatableResponse orderResponse = orderClient.createOrderWithoutAuth(new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72")));
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int orderNumber = orderResponse.extract().path("order.number");

        assertThat("User has created", statusCode, equalTo(SC_OK));
        assertThat("State if courier has created", success, equalTo(true));
        assertThat("State if courier has created", burgerName, notNullValue());
        assertThat("State if courier has created", orderNumber, notNullValue());
    }

    @Test
    @DisplayName("Creation of an order without ingredients by an authorized user")
    public void createOrderWithAuthUserWithoutIngredient() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrderWithAuth(new Order(List.of()), accessToken);
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String message = orderResponse.extract().path("message");

        assertThat("User has created", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("State if courier has created", success, equalTo(false));
        assertThat("State if courier has created", message, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Creation of an order without ingredients by an unauthorized user")
    public void createOrderWithoutAuthUserWithoutIngredient() {
        ValidatableResponse orderResponse = orderClient.createOrderWithoutAuth(new Order(List.of()));
        int statusCode = orderResponse.extract().statusCode();
        boolean success = orderResponse.extract().path("success");
        String message = orderResponse.extract().path("message");

        assertThat("User has created", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("State if courier has created", success, equalTo(false));
        assertThat("State if courier has created", message, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Creation of an order with an incorrect ingredient hash by an authorized user")
    public void createOrderWithAuthUserIncorrectHashIngredient() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse orderResponse = orderClient.createOrderWithAuth(new Order(List.of("61c0c5a71d1f82001bdaaa6", "61c0c5a71d1f8201bdaaa6f", "61c0c5a71d1f8001bdaaa72")), accessToken);
        int statusCode = orderResponse.extract().statusCode();
        assertThat("User has created", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}
