package com.bellj.springBootDemoService;

import com.bellj.springBootDemoService.controller.ClientsController;
import com.bellj.springBootDemoService.exception.BadRequestException;
import com.bellj.springBootDemoService.model.Client;
import com.bellj.springBootDemoService.testUtilities.DataSource;
import com.bellj.springBootDemoService.testUtilities.DatabaseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;



import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Tests to demonstrate integration testing. These tests rely on the whole Spring Boot application and its dependencies being operational.
 * To achieve this, an in memory H2 database is set up, see application-test.properties.
 * <br><br><i>Note.</i> This is very heavy and slow as it requires an entire Spring Boot server to be spun up for the tests.
 * This includes and additional dependencies such as a database. As such, to support this an H2 database is started in memory.
 * See how much slower this test suite is to load than the Controller tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SpringBootDemoServiceIntegrationTests {

    @Value("${spring.datasource.url}")
    private String URL;

    @Value("${spring.datasource.driverClassName}")
    private String driver;

    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientsController clientsController;

    private static DataSource DATA_SOURCE;


    @BeforeEach
    void beforeAll() {
        DATA_SOURCE = new DataSource(URL, driver, username, password);
        // Ensure the table exists and is empty
        DatabaseUtils.wipeAllTableData(DATA_SOURCE);

    }

    /**
     * Positive test case to prove the Spring container is correctly forming and injecting dependencies as needed.
     * <br><br>

     */
    @Test
    void contextLoads() {
        assertThat(clientsController).isNotNull();
    }


    /**
     * Positive test case to prove the client fetch works when hit via the spring routing and no clients exist
     */
    @Test
    void testGetAllClientsListEmptyList() throws Exception {
        // Given
        // Nothing to prepare

        // When + Then combined
        this.mockMvc.perform(get("/clients")).andExpect(status().isOk()).andExpect(content().string(containsString("[]")));
    }


    /**
     * Positive test case to prove the client fetch works when hit via the spring routing and some clients exist
     */
    @Test
    void testGetAllClientsListPopulatedList() throws Exception {
        // Given
        DatabaseUtils.addClient(DATA_SOURCE);
        // When + Then combined
        this.mockMvc.perform(
                get("/clients"))
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                containsString("[{\"id\":1,\"name\":\"test_name\",\"email\":\"test_email\"}]")));
    }

    /**
     * Positive test case to prove the client creation works when hit via the spring routing
     */
    @Test
    void testCreateClient() throws Exception {
        // Given
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(0);

        // When + Then combined
        this.mockMvc.perform(
                post("/clients")
                        .content(
                                asJsonString(new Client("test_name", "test_email")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().isCreated())
                .andExpect(
                        content().string(
                                containsString("{\"id\":1,\"name\":\"test_name\",\"email\":\"test_email\"}")));

        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(1);
    }

    /**
     * Positive test case to prove the client update works when hit via the spring routing
     */
    @Test
    void testUpdateClient() throws Exception {
        // Given
        DatabaseUtils.addClient(DATA_SOURCE);
        // Prove only 1 client exists
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(1);

        // When + Then combined
        this.mockMvc.perform(
                        put("/clients/1")
                                .content(
                                        asJsonString(new Client(1L, "new_test_name", "new_test_email")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().isOk())
                .andExpect(
                        content().string(
                                containsString("{\"id\":1,\"name\":\"new_test_name\",\"email\":\"new_test_email\"}")));

        // prove no more clients have been made
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(1);
    }

    /**
     * Negative test case to prove the client update prevents entity persistence for an unknown Client
     */
    @Test
    void testUpdateClientNoMatchingClient() throws Exception {
        // Given
        DatabaseUtils.addClient(DATA_SOURCE);
        // Prove only 1 client exists
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(1);

        // When + Then combined
        this.mockMvc.perform(
                        put("/clients/2")
                                .content(
                                        asJsonString(new Client(2L, "new_test_name", "new_test_email")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().isBadRequest())
                .andExpect(result -> assertNotNull(result.getResolvedException()))
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), "No client found by ID [2]"));

        // prove no more clients have been made
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(1);
    }


    /**
     * Positive test case to prove the deletion works when hit via the spring routing
     */
    @Test
    void testDelete() throws Exception{
        // Given
        DatabaseUtils.addClient(DATA_SOURCE);
        // Prove only 1 client exists
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(1);

        // When + Then combined
        this.mockMvc.perform(
                        delete("/clients/1"))
                .andExpect(
                        status().isOk());

        // prove no more clients exist
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(0);
    }

    /**
     * Positive test case to prove the deletion is stable when hit via the spring routing and no client matches the request
     */
    @Test
    void testDeleteNoMatchingClient() throws Exception{
        // Given
        // Prove no client exists
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(0);

        // When + Then combined
        this.mockMvc.perform(
                        delete("/clients/1"))
                .andExpect(
                        status().isOk());

        // If the deletion was not stable, we would not reach here.
        // prove still no clients exist
        assertThat(DatabaseUtils.countClients(DATA_SOURCE)).isEqualTo(0);
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
