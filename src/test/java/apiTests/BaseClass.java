package apiTests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import pojos.UserPojo;
import utils.ReadDataFromPropertiesFile;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class BaseClass extends ReadDataFromPropertiesFile {

    UserPojo user;
    String accessToken;
    String userId;

    @BeforeClass
    public void setup() {
        registerUser();
        generateToken();
    }

    public void registerUser() {

        user = new UserPojo(username, password);

        Response response = given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .relaxedHTTPSValidation()
                .body(user)
                .when()
                .post("/Account/v1/User");

        if (response.getStatusCode() == 201) {
            log.info("User is registered");
        } else if (response.getStatusCode() == 409) {
            log.error("User already exists");
        } else {
            log.error("Failed to register user. Status code: {}", response.getStatusCode());
        }

        userId = response.jsonPath().getString("userID");
        user = new UserPojo(username, password);
        response.then().log().all();
    }

    public void generateToken() {

        Response response = given()
                .contentType(ContentType.JSON)
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .body(user)
                .when()
                .post("/Account/v1/GenerateToken");

        response.then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("status", equalTo("Success"))
                .body("result", equalTo("User authorized successfully."))
                .log().all();

        accessToken = response.jsonPath().getString("token");
        log.info("Access token received");
    }

    @AfterClass
    public void deleteUser() {

        Response response = given()
                .contentType(ContentType.JSON)
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .auth().oauth2(accessToken)
                .pathParams("UserId", userId)
                .when()
                .delete("/Account/v1/User/{UserId}");

        response.then().statusCode(204).log().all();
        log.info("User deleted");
    }
}
