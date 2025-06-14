package Steps;

import apiController.*;

import data.BookStoreData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import utils.configController;
import utils.configController.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserFlowSteps {


    public BookStoreData bookStoreData;

    public UserFlowSteps() {
        this.bookStoreData = new BookStoreData();
    }
    List<HashMap<String,Object>> allBooksList=new ArrayList<>();

    HashMap<String,Object> bookDetails=new HashMap<>();
    @Given("^Adding the new book into the store after successful login of user into the system$")
    public void AddingTheNewBookIntoTheStoreAfterSuccessfulLoginOfUserIntoTheSystem()
    {
        Long uniqueIdentifier=System.nanoTime();
        bookDetails.put("bookName", "Book Title "+uniqueIdentifier);
        bookDetails.put("author","Book Author "+uniqueIdentifier);
        bookDetails.put("published_year",uniqueIdentifier);
        bookDetails.put("book_summary","Book summary for the book "+uniqueIdentifier);
        allBooksList.add(new HashMap<>(bookDetails));
    }

    @Given("Signup book store with new email and password")
    public void signupBookStoreWithNewEmailAndPassword() {

    }

    @When("^user tries to signup the store with (.*)$")
    public void userTriesToSignupTheStoreWith(String credentials) {
        if (credentials.equalsIgnoreCase("valid credentials")) {
            bookStoreData.setEmail(loginAndSignUpServiceContoller.generateEmailAndPassword(5) + "yahoo.com");
            bookStoreData.setPassword(loginAndSignUpServiceContoller.generateEmailAndPassword(15));
        } else if (credentials.equalsIgnoreCase("old credentials")) {
            bookStoreData.setEmail(configController.get("oldEmail"));
            bookStoreData.setPassword(loginAndSignUpServiceContoller.generateEmailAndPassword(15));
        } else if (credentials.equalsIgnoreCase("password credentials")) {
            bookStoreData.setPassword(loginAndSignUpServiceContoller.generateEmailAndPassword(15));
        } else if (credentials.equalsIgnoreCase("email credentials")) {
            bookStoreData.setEmail(loginAndSignUpServiceContoller.generateEmailAndPassword(5) + "yahoo.com");
        } else {
            bookStoreData.setEmail(loginAndSignUpServiceContoller.generateEmailAndPassword(5) + "yahoo.com");
            bookStoreData.setPassword(loginAndSignUpServiceContoller.generateEmailAndPassword(15));
        }
        Response response = loginAndSignUpServiceContoller.signUp(bookStoreData.getEmail(), bookStoreData.getPassword(), bookStoreData);
        bookStoreData.setSignUpResponse(response);
    }

    @Then("^validate whether a new user is created with response code (.*) and response message (.*) after signup$")
    public void validateWhetherANewUserIsCreatedWithResponseCodeAndResponseMessageAfterSignup(int statusCode, String message) {
        Assert.assertEquals(bookStoreData.getSignUpResponse().getStatusCode(), statusCode, "Sign up expected status code mismatch");

        if (statusCode == 200) {
            Assert.assertEquals(message, bookStoreData.getSignUpResponse().getBody().jsonPath().get("message").toString(), "User created successfully");
        } else if (statusCode == 400) {
            Assert.assertEquals(message, bookStoreData.getSignUpResponse().getBody().jsonPath().get("detail").toString(), "Email already registered");

        }
    }

    @Then("validate whether the user has logged in successfully with the response code (.*) and response message (.*)$")
    public void validateWhetherTheUserHasLoggedInSuccessfullyWithTheResponseCodeAndResponseMessage(int statusCode, String condition) {
        Assert.assertEquals(bookStoreData.getLoginResponse().getStatusCode(), statusCode,"The response code is not "+statusCode);
        switch (condition)
        {
            case "success":
                System.out.println("Successfully logged in"+"@@@@");
                bookStoreData.setAccessToken("Bearer "+bookStoreData.getLoginResponse().jsonPath().get("access_token"));
                System.out.println(bookStoreData.getAccessToken());
                Assert.assertNotNull(bookStoreData.getLoginResponse().jsonPath().get("access_token"),"Token is not generated after login");
                Assert.assertEquals(bookStoreData.getLoginResponse().jsonPath().get("token_type"),"bearer","Token generated type is not bearer");
                break;

            case "incorrectCredentials":
                Assert.assertEquals(bookStoreData.getLoginResponse().getStatusLine(),"HTTP/1.1 400 Bad Request","Response line is not as expected");
                Assert.assertEquals(bookStoreData.getLoginResponse().jsonPath().get("detail"),"Incorrect email or password","Incorrect error message in detail mismatch");
                break;

            case "missingParam":
                Assert.assertEquals(bookStoreData.getLoginResponse().getStatusLine(),"HTTP/1.1 422 Unprocessable Entity","Response line is not as expected");
                Assert.assertEquals(bookStoreData.getLoginResponse().jsonPath().get("detail.get(0).type"),"missing","Missing param error type is not shown");
                Assert.assertEquals(bookStoreData.getLoginResponse().jsonPath().get("detail.get(0).msg"),"Field required","Field required error should be shown");
                break;

        }
    }

    @When("^user tries to login to the bookStore using the  (.*)$")
    public void userTriesToLoginToTheBookStoreUsingTheNewCredentials(String credentials) {
        if(credentials.equalsIgnoreCase("noSignUpUser"))
        {
            bookStoreData.setEmail(loginAndSignUpServiceContoller.generateEmailAndPassword(10)+"@yahoo.com");
            bookStoreData.setPassword(loginAndSignUpServiceContoller.generateEmailAndPassword(8));
        }
        else if(credentials.equalsIgnoreCase("missingParam"))
        {
            bookStoreData.setEmail(null);
            bookStoreData.setPassword(null);
        }
        bookStoreData.setLoginResponse(loginAndSignUpServiceContoller.login(bookStoreData.getEmail(),bookStoreData.getPassword()));

    }
    @When("user tries to create a new book in the book store using the valid token of the user")
    public void userTriesToCreateANewBookInTheBookStoreUsingTheValidTokenOfTheUser() {
        bookStoreData.setAddBookResponse(bookApiServiceController.addNewBook(bookDetails,bookStoreData.getAccessToken(),bookStoreData));

    }

    @Given("^Sign up to the book store as the new user with email and password$")
    public void SignUpToTheBookStoreAsTheNewUserWithValidEmailAndPassword()
    {

    }




    @Then("verify whether the book is created and the response is success")
    public void verifyWhetherTheBookIsCreatedAndTheResponseIsSuccess() {

        Assert.assertNotNull(bookStoreData.getAddBookResponse().getBody().jsonPath().get("id"),"Unique id is not generated");
        bookDetails.put("createdBookId",bookStoreData.getAddBookResponse().getBody().jsonPath().get("id"));
        Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("name"),bookDetails.get("bookName"),"Book name  mismatch");
        Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("author"),bookDetails.get("author"),"Author name mismatch");
        Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("published_year"),bookDetails.get("published_year"),"Published year mismatch");
        Assert.assertEquals(bookStoreData.getAddBookResponse().getBody().jsonPath().get("book_summary"),bookDetails.get("book_summary"),"Book summary  mismatch");


    }

    @When("user tries to edit the book with the (.*)$")
    public void userTriesTiEditTheBookWithThe(String editAction) {
        if(editAction.equalsIgnoreCase("name"))
        {
            bookDetails.put("bookName","Book name is edited now");
        } else if (editAction.equalsIgnoreCase("author")) {
            bookDetails.put("author","Book author name is edited now");
        } else if (editAction.equalsIgnoreCase("bookSummary")) {
            bookDetails.put("book_summary","Book summary is edited now via update");
        } else if (editAction.equalsIgnoreCase("published_year")) {
            bookDetails.put("published_year",System.nanoTime());

        }
        if(editAction.equalsIgnoreCase("noAccessToken")){
            bookStoreData.setEditBookResponse(bookApiServiceController.editTheBook(bookDetails,null));
        }
        else {
            bookStoreData.setEditBookResponse(bookApiServiceController.editTheBook(bookDetails,bookStoreData.getAccessToken()));
        }
    }

    @Then("verifies whether the response is {int}")
    public void verifiesWhetherTheResponseIs(int statusCode) {
        Assert.assertEquals(bookStoreData.getLoginResponse().getStatusCode(), statusCode,"The response code is not "+statusCode);
        if(statusCode==200)
        {
            Assert.assertEquals(bookStoreData.getEditBookResponse().getStatusLine(),"HTTP/1.1 200 OK","Response line is not as expected for 200");
        }
        else if(statusCode==400)
        {
            Assert.assertEquals(bookStoreData.getEditBookResponse().getStatusLine(),"HTTP/1.1 400 Bad Request","Response line is not as expected for 400");
        } else if (statusCode==403) {
            Assert.assertEquals(bookStoreData.getEditBookResponse().getStatusLine(),"HTTP/1.1 403 Forbidden","Response line is not as expected for 403");
            Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("detail"),"Not authenticated","No error message for 403");
        }
    }

    @And("verify the edited book details values in response for editing (.*)$")
    public void verifyTheEditedBookDetailsValuesInResponseForEditingName(String condition) {
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("name"),bookDetails.get("bookName"),"Book name  mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("author"),bookDetails.get("author"),"Author name mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("published_year"),bookDetails.get("published_year"),"Published year mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("book_summary"),bookDetails.get("book_summary"),"Book summary  mismatch");
        Assert.assertEquals(bookStoreData.getEditBookResponse().getBody().jsonPath().get("id"),bookDetails.get("createdBookId"),"Book id is different to the request");

    }

    @When("user tries to fetch the book created using the id created for that book")
    public void userTriesToFetchTheBookCreatedUsingTheIdCreatedForThatBook() {
        bookStoreData.setGetBookDetailsByIdResponse(bookApiServiceController.getBookDetailsById(bookDetails, bookStoreData.getAccessToken()));

    }

    @Then("verify whether the response is success")
    public void verifyWhetherTheResponseIsSuccess() {
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getBody().jsonPath().get("name"),bookDetails.get("bookName"),"Book name  mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getBody().jsonPath().get("author"),bookDetails.get("author"),"Author name mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getBody().jsonPath().get("published_year"),bookDetails.get("published_year"),"Published year mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getBody().jsonPath().get("book_summary"),bookDetails.get("book_summary"),"Book summary  mismatch");
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getBody().jsonPath().get("id"),bookDetails.get("createdBookId"),"Book id is different to the request");

    }

    @When("user tries to delete the book added in the bookstore using the id")
    public void userTriesToDeleteTheBookAddedInTheBookstoreUsingTheId() {
        bookStoreData.setDeleteBookResponse(bookApiServiceController.deleteTheBookById(bookDetails.get("createdBookId").toString(), bookStoreData.getAccessToken()));

    }

    @And("verify the response after deleting the book should be (.*)$")
    public void verifyTheResponseAfterDeletingTheBookShouldBeSuccess(String condition) {
        if(condition.equalsIgnoreCase("Success"))
        {
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusCode(), 200,"The response code is not 200");
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusLine(),"HTTP/1.1 200 OK","Response line is not as expected");
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getBody().jsonPath().get("message"),"Book deleted successfully","Book not deleted yet");
        } else if (condition.equalsIgnoreCase("notFound")) {
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusCode(), 404,"The response code is not 404");
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getStatusLine(),"HTTP/1.1 404 Not Found","Response line is not as expected");
            Assert.assertEquals(bookStoreData.getDeleteBookResponse().getBody().jsonPath().get("detail"),"Book not found","Book should not be deleted");
        }

    }

    @When("the user tries to edit the (.*) of the book$")
    public void theUserTriesToEditTheNameOfTheBook() {

    }

    @When("fetch all the books that added to the book store")
    public void fetchAllTheBooksThatAddedToTheBookStore() {
        bookStoreData.setFetchAllBooks(bookApiServiceController.getAllBooks(bookStoreData.getAccessToken()));

    }

    @Then("verify the details of books that listed")
    public void verifyTheDetailsOfBooksThatListed() {
        for(HashMap<String,Object> eachData:allBooksList)
        {
            System.out.println(bookStoreData.getFetchAllBooks().contains(eachData));
        }
    }

    @When("the user tries to get the book with wrong id")
    public void theUserTriesToGetTheBookWithWrongId() {
        bookStoreData.setGetBookDetailsByIdResponse(bookApiServiceController.getBookDetailsByInvalidId(bookDetails, bookStoreData.getAccessToken()));

    }

    @Then("verify whether we do not get a correct response")
    public void verifyWhetherWeDoNotGetACorrectResponse() {
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getStatusCode(),422);
        Assert.assertEquals(bookStoreData.getGetBookDetailsByIdResponse().getStatusLine(),"HTTP/1.1 422 Unprocessable Entity");


    }
}
