package apiTests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojos.BookPojo;

import java.util.List;

import static io.restassured.RestAssured.given;

// E2E flow: get books -> add book -> delete book -> verify
public class E2ETests extends BaseClass {

    String bookId;

    private RequestSpecification requestSpec() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .relaxedHTTPSValidation()
                .auth().oauth2(accessToken);
    }

    @Test
    public void getAllBooks() {
        Response response = requestSpec()
                .get("/BookStore/v1/Books");

        response.then()
                .statusCode(200)
                .log().all();

        List<BookPojo> books = response.jsonPath().getList("books", BookPojo.class);
        Assert.assertFalse(books.isEmpty());

        int idx = (int) (Math.random() * books.size());
        bookId = books.get(idx).getIsbn();

        books.forEach(System.out::println);
    }

}
