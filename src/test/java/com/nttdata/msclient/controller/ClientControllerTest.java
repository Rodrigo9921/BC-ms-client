package com.nttdata.msclient.controller;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.dto.ClientSummaryDto;
import com.nttdata.msclient.dto.PassiveProductDto;
import com.nttdata.msclient.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    @InjectMocks
    ClientController clientController;

    @Mock
    ClientService clientService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToController(clientController).build();
    }

    @Test
    public void testGetAllClients() {
        ClientDto clientDto1 = new ClientDto();
        clientDto1.setClientId("client1");
        ClientDto clientDto2 = new ClientDto();
        clientDto2.setClientId("client2");
        when(clientService.getAllClients()).thenReturn(Flux.just(clientDto1, clientDto2));

        webTestClient.get().uri("/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientDto.class)
                .hasSize(2)
                .contains(clientDto1, clientDto2);
    }

    @Test
    void getActiveProductById() {
        // Arrange
        String clientId = "clientId1";
        String productId = "productId1";
        ActiveProductDto activeProductDto = new ActiveProductDto(); // replace with actual active product DTO
        given(clientService.getActiveProductById(clientId, productId)).willReturn(Mono.just(activeProductDto));

        // Act & Assert
        webTestClient.get()
                .uri("/clients/{clientId}/activeProducts/{id}", clientId, productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActiveProductDto.class).isEqualTo(activeProductDto);
    }

    @Test
    void getPassiveProductById() {
        // Arrange
        String clientId = "clientId1";
        String productId = "productId1";
        PassiveProductDto passiveProductDto = new PassiveProductDto(); // replace with actual passive product DTO
        given(clientService.getPassiveProductById(clientId, productId)).willReturn(Mono.just(passiveProductDto));

        // Act & Assert
        webTestClient.get()
                .uri("/clients/{clientId}/passiveProducts/{id}", clientId, productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PassiveProductDto.class).isEqualTo(passiveProductDto);
    }
    @Test
    public void getClientSummaryTest() {
        String clientId = "1";

        ClientDto clientDto = new ClientDto();
        // ... inicializa tu clientDto aquí ...

        List<ActiveProductDto> activeProducts = new ArrayList<>();
        // ... inicializa tu lista de activeProducts aquí ...

        List<PassiveProductDto> passiveProducts = new ArrayList<>();
        // ... inicializa tu lista de passiveProducts aquí ...

        ClientSummaryDto clientSummaryDto = new ClientSummaryDto(clientDto, activeProducts, passiveProducts);

        when(clientService.getClientSummary(clientId)).thenReturn(Mono.just(clientSummaryDto));

        webTestClient.get()
                .uri("/clients/{clientId}/summary", clientId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientSummaryDto.class)
                .isEqualTo(clientSummaryDto);

        verify(clientService, times(1)).getClientSummary(clientId);
    }
}