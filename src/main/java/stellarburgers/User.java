package stellarburgers;

import com.github.javafaker.Faker;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Data;

import java.util.Locale;

@Data
@Builder
public class User {
    public String email;
    public String password;
    public String name;

    @Step("Create users with random credentials")
    public static User getRandomUser() {
        Faker faker = new Faker(new Locale("en"));
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().firstName();
        Allure.addAttachment("Login", email);
        Allure.addAttachment("Password", password);
        Allure.addAttachment("Name", name);
        return new User(email, password, name);
    }
}
