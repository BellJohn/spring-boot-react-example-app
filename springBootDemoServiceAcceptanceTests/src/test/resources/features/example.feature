Feature: An example

  Background:
    Given no clients are registered in the database


  Scenario: Can fetch empty clients
    When a GET request is sent to "/clients"
    Then the responseBody matches "[]"
    And the responseCode is 200

  Scenario: Can fetch all known clients
    Given 3 clients are registered in the database
    When a GET request is sent to "/clients"
    # Doing string matches like this isn't good practice. Should really pull elements out by their JSON path
    Then the responseBody matches "[{\"id\":1,\"name\":\"test_name\",\"email\":\"test_email\"},{\"id\":2,\"name\":\"test_name\",\"email\":\"test_email\"},{\"id\":3,\"name\":\"test_name\",\"email\":\"test_email\"}]"
    And the responseCode is 200

  Scenario: Can get by ID
    Given 3 clients are registered in the database
    When a GET request is sent to "/clients/2"
    Then the responseBody matches "{\"id\":2,\"name\":\"test_name\",\"email\":\"test_email\"}"
    And the responseCode is 200

  Scenario: Can create a new user
    When a POST request is sent to "/clients" with the body
    """
       {"name":"test_name","email":"test_email"}
    """
    Then the responseBody matches "{\"id\":1,\"name\":\"test_name\",\"email\":\"test_email\"}"
    And the responseCode is 201

  Scenario: Can update a user
    Given 1 clients are registered in the database
    When a PUT request is sent to "/clients" with ID 1 with body
    """
       {"id":1,"name":"new_test_name","email":"new_test_email"}
    """
    Then the responseBody matches "{\"id\":1,\"name\":\"new_test_name\",\"email\":\"new_test_email\"}"
    And the responseCode is 200

    Scenario: Can delete a user
      Given 1 clients are registered in the database
      When a DELETE request is sent to "/clients" with ID 1
      Then the responseBody matches ""
      And the responseCode is 200