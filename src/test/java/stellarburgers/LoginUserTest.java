package stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class LoginUserTest {
    UserClient userClient;
    private User user;
    final String email = RandomStringUtils.randomAlphabetic(10) + "@random.com";
    final String password = RandomStringUtils.randomAlphabetic(10);
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(email, password, "TestName");
        userClient.createUser(user);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("The user can log in with the correct data")
    public void userCanLoginWithValidCredentials() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        String refreshToken = loginResponse.extract().path("refreshToken");
        String email = loginResponse.extract().path("user.email");
        String name = loginResponse.extract().path("user.name");

        assertThat("Status code is not correct", statusCode, equalTo(SC_OK));
        assertThat("Authorization attempt failed", success, equalTo(true));
        assertThat("AccessToken is not correct", accessToken, startsWith("Bearer"));
        assertThat("RefreshToken is not correct", refreshToken, notNullValue());
        assertThat("Email is not correct ", email, equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
        assertThat("Name is not correct", name, equalTo(user.getName()));
    }

    @Test
    @DisplayName("The user cannot log in without filling in the required field {password}")
    public void userCannotLoginWithoutPassword() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(401));
        assertThat("The login attempt must fail", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("The user cannot log in without filling in the required field {email}")
    public void userCannotLoginWithoutEmail() {
        UserCredentials credentials = UserCredentials.builder()
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("The login attempt must fail", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("User cannot login with incorrect password")
    public void userCannotLoginWithIncorrectPassword() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(user.getPassword() + "5")
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("The login attempt must fail", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("User cannot login with incorrect email")
    public void userCannotLoginWithIncorrectEmail() {
        UserCredentials credentials = UserCredentials.builder()
                .email("H" + user.getEmail())
                .password(user.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentials);

        int statusCode = loginResponse.extract().statusCode();
        boolean success = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("The login attempt must fail", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("email or password are incorrect"));
    }
}
