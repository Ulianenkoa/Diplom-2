package stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserTest {
    UserClient userClient;
    private User user;
    String accessToken;

    @Before
    public void setUp(){
         userClient = new UserClient();

    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("User must be created")
    public void userMustBeCreatedTest(){
        user = User.getRandomUser();
        ValidatableResponse createResponse = userClient.createUser(user);

        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String accessToken = createResponse.extract().path("accessToken");
        String refreshToken = createResponse.extract().path("refreshToken");
        String email = createResponse.extract().path("user.email");
        String name = createResponse.extract().path("user.name");

        assertThat("Status code is not correct", statusCode, equalTo(SC_OK));
        assertThat("User not created", success, equalTo(true));
        assertThat("AccessToken is not correct", accessToken,startsWith("Bearer"));
        assertThat("RefreshToken is not correct", refreshToken, notNullValue());
        assertThat("Email is not correct ", email, equalTo(user.getEmail()));
        assertThat("Name is not correct", name, equalTo(user.getName()));
    }

    @Test
    @DisplayName("User must be Unique")
    public void userMustBeUniqueTest(){
        user = User.getRandomUser();
        userClient.createUser(user);
        ValidatableResponse createResponse = userClient.createUser(user);

        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User must not be created", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("User already exists"));
    }

    @Test
    @DisplayName("A user cannot be created without filling in a required field {email}")
    public void userCannotCreatedWithoutEmailTest(){
        user = User.builder()
                .password(RandomStringUtils.randomAlphabetic(10))
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse createResponse = userClient.createUser(user);
        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User must not be created", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("A user cannot be created without filling in a required field {password}")
    public void userCannotCreatedWithoutPasswordTest(){
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(7)+"@random.com")
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse createResponse = userClient.createUser(user);
        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User must not be created", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("A user cannot be created without filling in a required field {name}")
    public void userCannotCreatedWithoutNameTest(){
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(7)+"@random.com")
                .password(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse createResponse = userClient.createUser(user);
        int statusCode = createResponse.extract().statusCode();
        boolean success = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        assertThat("Status code is not correct", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User must not be created", success, equalTo(false));
        assertThat("The error massage is not correct", message, equalTo("Email, password and name are required fields"));
    }
}
