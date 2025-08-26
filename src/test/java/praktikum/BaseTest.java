package praktikum;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import org.junit.Before;
import config.ApiEndpoints;

public class BaseTest {

    @Before
    public void startUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(ApiEndpoints.HOST)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.config = RestAssured
                .config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }
}
