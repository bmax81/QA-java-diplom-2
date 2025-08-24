package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import net.datafaker.Faker;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.User;
import steps.UserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class LoginUserTests extends BaseTest {

    private final UserSteps userSteps = new UserSteps();
    private User user;
    private boolean isUserCreated = false;
    Faker faker = new Faker();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = new User();
        user.setEmail(faker.internet().safeEmailAddress())
                .setPassword(faker.internet().password())
                .setName(faker.name().username());
        userSteps.createUser(user);
        isUserCreated = true;
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void shouldLoginExistingUserTest() {
        userSteps
                .loginUser(user)
                .statusCode(SC_OK)
                .body("refreshToken", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным email")
    public void shouldNotLoginUserWithWrongEmailTest() {
        User wrongEmailUser = new User()
                .setEmail("1234")
                .setPassword(user.getPassword());
        userSteps
                .loginUser(wrongEmailUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    public void shouldNotLoginWithWrongPasswordTest() {
        User wrongPasswordUser = new User()
                .setPassword("1234")
                .setEmail(user.getEmail());
        userSteps
                .loginUser(wrongPasswordUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (isUserCreated) {
            String token = userSteps.loginUser(user)
                    .extract().body().path("accessToken");
            user.setToken(token);
            userSteps.deleteUser(user);
        }
    }
}