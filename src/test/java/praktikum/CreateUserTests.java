package praktikum;

import io.qameta.allure.Description;
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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class CreateUserTests extends BaseTest {

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
        isUserCreated = false;
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест создает нового уникального пользования")
    public void shouldCreateNewUserTest() {
        userSteps
                .createUser(user)
                .statusCode(SC_OK)
                .body("success", Matchers.is(true));
        isUserCreated = true;
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Тест пытается создать уже зарегистрированного пользователя")
    public void shouldNotCreateDuplicateUserTest() {
        userSteps
                .createUser(user);
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", Matchers.is(false))
                .body("message", Matchers.equalTo("User already exists"));
        isUserCreated = true;
    }

    @Test
    @DisplayName("Создание пользователя без одного из обязательных полей (без email)")
    @Description("Попытка создать пользователя без обязательного поля email")
    public void shouldNotCreateUserWithoutEmailTest() {
        user.setEmail(null);
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", Matchers.is(false))
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
        isUserCreated = false;
    }

    @Test
    @DisplayName("Создание пользователя без одного из обязательных полей (без password)")
    @Description("Попытка создать пользователя без обязательного поля password")
    public void shouldNotCreateUserWithoutPasswordTest() {
        user.setPassword(null);
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", Matchers.is(false))
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
        isUserCreated = false;
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Попытка создать пользователя без одного из обязательных полей - имени")
    public void shouldNotCreateUserWithoutNameTest() {
        user.setName(null);
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", Matchers.is(false))
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
        isUserCreated = false;
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