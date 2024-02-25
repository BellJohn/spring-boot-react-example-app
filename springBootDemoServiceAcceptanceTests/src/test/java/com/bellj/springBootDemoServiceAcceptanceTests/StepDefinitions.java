package com.bellj.springBootDemoServiceAcceptanceTests;

import com.bellj.springBootDemoServiceAcceptanceTests.testUtilities.DataSource;
import com.bellj.springBootDemoServiceAcceptanceTests.testUtilities.DatabaseUtils;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;


public class StepDefinitions {
    private static final String DATABASE_URL = ConfigProperties.getDatabaseUrl();
    private static final int DATABASE_PORT = ConfigProperties.getDatabasePort();

    private static final String API_URL = ConfigProperties.getApiUrl();
    private static final int API_PORT = ConfigProperties.getApiPort();
    private static final String DATABASE_USERNAME = ConfigProperties.getDatabaseUsername();
    private static final String DATABASE_PASSWORD = ConfigProperties.getDatabasePassword();
    private static final String DRIVER = ConfigProperties.getDriver();

    private static Response response;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = API_URL;
        RestAssured.port = API_PORT;

        DataSource dataSource = new DataSource(DATABASE_URL, DRIVER, DATABASE_USERNAME, DATABASE_PASSWORD);
        DatabaseUtils.wipeAllTableData(dataSource);
        response = null;
    }

    @Given("no clients are registered in the database")
    public void noClientsExist() {
        DataSource dataSource = new DataSource(DATABASE_URL, DRIVER, DATABASE_USERNAME, DATABASE_PASSWORD);
        DatabaseUtils.wipeAllTableData(dataSource);
    }

    @Given("{int} clients are registered in the database")
    public void registerClients(int num) {
        DataSource dataSource = new DataSource(DATABASE_URL, DRIVER, DATABASE_USERNAME, DATABASE_PASSWORD);
        for (int i = 0; i < num; i++) {
            DatabaseUtils.addClient(dataSource);
        }
        DatabaseUtils.readClients(dataSource);
    }

    @When("a GET request is sent to {string}")
    public void sendGetRequest(String url) {
        String fullURL = String.format("%s:%d%s", RestAssured.baseURI, RestAssured.port, url);
        response = RestAssured.get(fullURL);
    }

    @When("a POST request is sent to {string} with the body")
    public void sendPostRequestWithBody(String url, String body) {
        RequestSpecification request = RestAssured.given().body(body)
                .contentType("application/json");
        String fullURL = String.format("%s:%d%s", RestAssured.baseURI, RestAssured.port, url);
        response = request.post(fullURL);
    }

    @When("a PUT request is sent to {string} with ID {int} with body")
    public void sendPutRequestWithBody(String url, Integer id, String body) {
        RequestSpecification request = RestAssured.given().body(body)
                .contentType("application/json");
        String fullURL = String.format("%s:%d%s/%d", RestAssured.baseURI, RestAssured.port, url, id);
        response = request.put(fullURL);
    }

    @When("a DELETE request is sent to {string} with ID {int}")
    public void sendDeleteRequest(String url, Integer id) {
        RequestSpecification request = RestAssured.given()
                .contentType("application/json");
        String fullURL = String.format("%s:%d%s/%d", RestAssured.baseURI, RestAssured.port, url, id);
        System.out.println("Sending to " + fullURL);
        response = request.delete(fullURL);
    }

    @Then("the responseCode is {int}")
    public void responseCodeMatches(Integer expected) {
        assertThat(response.statusCode(), equalTo(expected));
    }

    @Then("the responseBody matches {string}")
    public void responseBodyMatches(String expected) {
        assertThat(response.body().print(), equalToIgnoringCase(expected));
    }


}
