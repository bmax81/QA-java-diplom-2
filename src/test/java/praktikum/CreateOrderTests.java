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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import models.Order;
import models.User;
import steps.OrderSteps;
import steps.UserSteps;

import static org.apache.http.HttpStatus.*;

public class CreateOrderTests extends BaseTest {
    private final OrderSteps orderSteps = new OrderSteps();
    private Order order;
    private final UserSteps userSteps = new UserSteps();
    private User user;
    private boolean isUserCreated = false;
    Faker faker = new Faker();

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        order = new Order();
        if (testName.getMethodName().contains("WithLogin")) {
            user = new User()
                    .setEmail(faker.internet().safeEmailAddress())
                    .setPassword(faker.internet().password())
                    .setName(faker.name().username());

            userSteps.createUser(user);
            isUserCreated = true;
            String accessToken = userSteps.loginUser(user)
                    .extract()
                    .path("accessToken");
            order.setAccessToken("Bearer " + accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Создание заказа без авторизации с ингредиентами")
    public void shouldNotCreateOrderWithoutLoginTest() {
        String[] randomIngredients = orderSteps.getRandomIngredientIds(5);
        order.setIngredients(randomIngredients);
        orderSteps
                .createOrder(order)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is(false));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Создание заказа с авторизацией с ингредиентами")
    public void shouldCreateOrderWithLoginTest() {
        String[] randomIngredients = orderSteps.getRandomIngredientIds(5);
        order.setIngredients(randomIngredients);
        orderSteps
                .createOrder(order)
                .statusCode(SC_OK)
                .body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Создание заказа с авторизацией и без ингредиентов")
    public void shouldNotCreateOrderWithoutIngredientsWithLoginTest() {
        orderSteps
                .createOrder(order)
                .statusCode(SC_BAD_REQUEST)
                .body("success", Matchers.is(false))
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Создание заказа с авторизацией с неверным хешем ингредиентов")
    public void shouldNotCreateOrderWithInvalidIngredientHashWithLoginTest() {
        String[] wrongIngredients = {"invalid_hash_1", "invalid_hash_2"};
        order.setIngredients(wrongIngredients);
        orderSteps
                .createOrder(order)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
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
