package apiTests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojos.AddListOfBooksPojo;
import pojos.BookPojo;
import pojos.DeleteBookPojo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Test(dependsOnMethods = "getAllBooks")
    public void addBook() {
        AddListOfBooksPojo books = new AddListOfBooksPojo(Collections.singletonList(Map.of("isbn", bookId)), userId);

        requestSpec()
                .body(books)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .statusCode(201);
    }

    @Test(dependsOnMethods = "addBook")
    public void deleteBook() {

        DeleteBookPojo payload = new DeleteBookPojo(bookId, userId);

        requestSpec()
                .body(payload)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteBook")
    public void verifyDeletion() {
        Response response = requestSpec()
                .pathParams("UserId", userId)
                .when()
                .get("/Account/v1/User/{UserId}");

//        List<BookPojo> books = response.jsonPath().getList("books", BookPojo.class);
//        Assert.assertFalse(books.stream()
//                .anyMatch(book -> book.getIsbn().equals(bookId)));

        List<String> bookIds = response.jsonPath().getList("books.isbn");
        Assert.assertFalse(bookIds.contains(bookId));
    }

}
