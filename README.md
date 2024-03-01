# Spring Boot + React Example App

### Base Service built from [spring-boot-react by Eugen](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-react)

This repo builds upon the base React and SpringBoot service developed by Eugen to extend it with examples of testing
methods.

## Unit Tests

The controller test code
under [springBootDemoService - Controller Test](springBootDemoService/src/test/java/com/bellj/springBootDemoService/controller/ClientsControllerTest.java)
demonstrates unit testing best practices.
Dependencies are mocked out and the scope of the test is restricted to just the contract provided by the Controller
class without consideration to the Spring wrapper.

These don't rely on any active database connection.

## Integration Tests

The Integration Test code
under [springBootDemoService - Integration Test](springBootDemoService/src/test/java/com/bellj/springBootDemoService/SpringBootDemoServiceIntegrationTests.java)
demonstrates Integration testing with respect to the wrapping Spring container and the expectation of some kind of data
source backing the JPA repository.
In this situation we don't care about the particular technology we will use for data persistence in production too much
and can make do with an in memory H2 database operating in MySQL mode.

These rely on a database connection defined by [application-test.properties](springBootDemoService/src/test/resources/application-test.properties)

## Acceptance BDD Tests

The Cucumber acceptance tests available
in [springBootDemoServiceAcceptanceTests - example.feature](springBootDemoServiceAcceptanceTests/src/test/resources/features/example.feature)
explores the API as it will be deployed in production.
It makes use of the actual database type that will be used (in this case a MySQL database available in AWS). By having
access to that we can manage the state of the database ahead of and after every test though this is computationally
expensive (especially the way I've implemented it).
These tests prove there is no difference in behaviour when the fullstack is put in place and the contract is provided
correctly.

These rely on a database connection defined by [application.properties](springBootDemoService/src/main/resources/application.properties)

## UI Unit Tests

The files under [frontend - src](frontend/src) which match the pattern ```*.test.js``` are similar in function to the
Unit Tests under the service itself. (I'm not familiar with React, so they are very lightweight).
These prove the correct rendering of the content in the same way that any single unit of code abides by its function.
While further up the stack, these are still unit tests, not acceptance tests.

These don't rely on any database connection.

## Missing End to End Acceptance Tests

A final set of tests could be implemented which would prove the system end to end. This could be done using a tool like
Selenium to simulate user actions on the front end and validate the DOM is correctly formed when including real data
from a database.

