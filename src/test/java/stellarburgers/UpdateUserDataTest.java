package stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UpdateUserDataTest {
    UserClient userClient;
    private User user;
    String accessToken;
    ValidatableResponse loginResponse;

    @Before
    public void setUp(){
        userClient = new UserClient();
        user = User.getRandomUser();
        userClient.createUser(user);

    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Changing user data with authorization")
    public void updateAuthorizedUserDataTest(){
        UserCredentials credentials= UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        loginResponse = userClient.loginUser(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        User newUser = User.getRandomUser();
        ValidatableResponse updateResponse = userClient.updateUserInfo(accessToken,newUser);

        int statusCode = updateResponse.extract().statusCode();
        boolean success = updateResponse.extract().path("success");
        String email = updateResponse.extract().path("user.email");
        String name = updateResponse.extract().path("user.name");

        assertThat("Status code is not correct", statusCode, equalTo(SC_OK));
        assertThat("The data must be changed", success, equalTo(true));
        assertThat("Email is not correct", email, equalTo(newUser.getEmail()));
        assertThat("Name is not correct", name, equalTo(newUser.getName()));
    }

    @Test
    @DisplayName("Changing user data without authorization")
    public void updateUnauthorizedUserDataTest(){
        accessToken = "";
        User newUser = User.getRandomUser();
        ValidatableResponse updateResponse = userClient.updateUserInfo(accessToken,newUser);

        int statusCode = updateResponse.extract().statusCode();
        boolean success = updateResponse.extract().path("success");
        String message = updateResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("The data must not be changed", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Changing user data with authorization by specifying an already used email")
    public void updateAuthorizedUserDataWithExistingEmailTest(){
        UserCredentials credentials= UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        loginResponse = userClient.loginUser(credentials);
        accessToken = loginResponse.extract().path("accessToken");
        User newUser = User.builder()
                .email("ulianenkoa@gmail.com")
                .password(user.getPassword())
                .name(user.getName())
                .build();
        ValidatableResponse updateResponse = userClient.updateUserInfo(accessToken,newUser);

        int statusCode = updateResponse.extract().statusCode();
        boolean success = updateResponse.extract().path("success");
        String message = updateResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("The data must not be changed", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("User with such email already exists"));
    }

}
