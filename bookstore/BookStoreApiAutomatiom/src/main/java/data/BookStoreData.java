package data;

import io.restassured.response.Response;
import lombok.Data;
import java.util.List;

@Data
public class BookStoreData {
    private String email;
    private String password;
    private Response signUpResponse;
    private Response loginResponse;
    private String accessToken;
    private Response addBookResponse;
    private Response editBookResponse;
    private Response getBookDetailsByIdResponse;
    private Response deleteBookResponse;
    private List<Response> fetchAllBooks;
}
