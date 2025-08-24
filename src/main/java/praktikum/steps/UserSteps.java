package steps;

import config.ApiEndpoints;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import config.ApiEndpoints;
import models.User;

import static io.restassured.RestAssured.given;

public class UserSteps {

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .body(user)
                .when()
                .post(ApiEndpoints.CREATE_USER)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(User user) {
        return given()
                .body(user)
                .when()
                .post(ApiEndpoints.LOGIN_USER)
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(User user) {
        given()
                .header("Authorization", user.getToken())
                .when()
                .delete(ApiEndpoints.DELETE_USER)
                .then();
    }
}