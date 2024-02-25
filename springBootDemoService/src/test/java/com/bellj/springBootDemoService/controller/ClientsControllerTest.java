package com.bellj.springBootDemoService.controller;

import com.bellj.springBootDemoService.model.Client;
import com.bellj.springBootDemoService.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests to demonstrate unit testing. These are of as limited scope as can be achieved.
 * All dependencies of the tests are mocked out to limit the scope of the tests, that way we are not reliant upon the actual implementation of the dependencies, reducing coupling.
 * These are different to the integration tests because of this.
 */
@ExtendWith(MockitoExtension.class)
public class ClientsControllerTest {

    @Mock
    private ClientRepository mockClientRepository;

    private ClientsController clientsController;

    @BeforeEach
    void setup() {
        clientsController = new ClientsController(mockClientRepository);
    }

    /**
     * Positive test case to prove the controller's getClients method returns expected {@link Client} objects
     */
    @Test
    void testGetClientsList() {
        //Given
        List<Client> expectedClients = List.of(new Client(1L, "Client_1", "client_1@example.com"), new Client(2L, "Client_2", "client_2@example.com"));
        when(mockClientRepository.findAll()).thenReturn(expectedClients);

        //When
        List<Client> result = clientsController.getClients();

        //Then
        assertThat(result).containsAll(expectedClients);
    }

    /**
     * Positive test case to prove the controller's getClient(ID) method returns a single expected {@link Client} object
     */
    @Test
    void testGetClient() {
        //Given
        List<Client> availableClients = List.of(new Client(1L, "Client_1", "client_1@example.com"));
        when(mockClientRepository.findById(1L)).thenReturn(availableClients.stream().findFirst());

        //When
        Client result = clientsController.getClient(1L);

        //Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Client_1");
        assertThat(result.getEmail()).isEqualTo("client_1@example.com");
    }

    /**
     * Positive test case to prove the controller's client creation method results in a Client entity being persisted and an appropriate response is formed
     *
     * @throws URISyntaxException
     */
    @Test
    void testCreateClient() throws URISyntaxException {
        //Given
        // Make the mocked save method return the given entity upon invocation
        when(mockClientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);

        //When
        ResponseEntity<Client> result = clientsController.createClient(new Client(1L, "Client_1", "client_1@example.com"));

        //Then
        // Prove the repository had a Client object saved in it one time
        verify(mockClientRepository, times(1)).save(any(Client.class));

        // Prove the status code is correct
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Prove the body is all well-formed
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("client_1@example.com");
        assertThat(result.getBody().getName()).isEqualTo("Client_1");

        // Prove the headers are well-formed
        assertThat(result.getHeaders().size()).isEqualTo(1);
        assertThat(result.getHeaders().get("Location")).isEqualTo(List.of("/clients/1"));
    }

    /**
     * Positive test case to prove the controller's client update method results in the modification of a Client entity.
     */
    @Test
    void testUpdateClient() {
        // Given
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(new Client(1L, "Client_1", "client_1@example.com")));
        when(mockClientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        ResponseEntity<Client> result = clientsController.updateClient(1L, new Client(1L, "new_client_1_name", "new_client_1@example.com"));


        //Then
        /**
         * These validations are not ideal as we are strongly coupling the test implementation to the implementation of the controller.
         * What happens if we changed out how the persistence mechanism was implemented?
         * It does give us a good separation from the underlying persistence technology though. By mocking out the repository, we don't need to worry about whether it's being stored in MySQL / Dynamo / etc which is good.
         */
        // Prove the repository found the expected Client object
        verify(mockClientRepository, times(1)).findById(1L);
        // Prove the repository had a Client object saved in it one time
        verify(mockClientRepository, times(1)).save(any(Client.class));

        /**
         * These validations are OK as they are testing the contract exposed by the methods under test.
         */
        // Prove the status code is correct
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("new_client_1@example.com");
        assertThat(result.getBody().getName()).isEqualTo("new_client_1_name");

        // Prove the headers are empty
        assertTrue(result.getHeaders().isEmpty());
    }

    /**
     * Positive test case that proves the deletion of a client results in a well-formed response
     */
    @Test
    void testDeleteClient() {
        // Given
        // Don't need to mock anything out to get a successful result

        // When
        ResponseEntity<String> result = clientsController.deleteClient(1L);

        // Then
        // Prove the repository was asked to delete a record
        verify(mockClientRepository, times(1)).deleteById(anyLong());

        // Prove the response payload is well-formed
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();
    }


    /**
     * Negative test case to prove the update method fails when a record which doesn't exist is being updated.
     * This prevents an unknown record being inserted into the database when it shouldn't
     */
    @Test
    void testUpdateClientThrowsException() {
        // Given
        // Simulate the repository not containing the record being updated
        when(mockClientRepository.findById(1L)).thenThrow(new RuntimeException("Test Exception - Can't find that record"));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> clientsController.updateClient(1L, new Client(1L, "new_client_1_name", "new_client_1@example.com")));

        // Then
        assertThat(exception.getMessage()).isEqualTo("Test Exception - Can't find that record");
    }
}
