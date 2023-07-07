package com.nttdata.msclient.controller;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.dto.ClientSummaryDto;
import com.nttdata.msclient.dto.PassiveProductDto;
import com.nttdata.msclient.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ClientDto> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{clientId}")
    public Mono<ResponseEntity<ClientDto>> getClientById(@PathVariable String clientId) {
        return clientService.getClientById(clientId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        return clientService.createClient(clientDto);
    }

    @PutMapping("/{clientId}")
    public Mono<ResponseEntity<ClientDto>> updateClient(@PathVariable String clientId, @RequestBody ClientDto clientDto) {
        return clientService.updateClient(clientId, clientDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{clientId}")
    public Mono<ResponseEntity<Void>> deleteClient(@PathVariable String clientId) {
        return clientService.deleteClient(clientId)
                .thenReturn(ResponseEntity.noContent().build());
    }
    //Passive and Active products
    @PostMapping("/{clientId}/passiveProducts")
    public Mono<ResponseEntity<ClientDto>> addPassiveProduct(@PathVariable String clientId, @RequestBody PassiveProductDto passiveProductDto) {
        return clientService.addPassiveProduct(clientId, passiveProductDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{clientId}/activeProducts")
    public Mono<ResponseEntity<ClientDto>> addActiveProduct(@PathVariable String clientId, @RequestBody ActiveProductDto activeProductDto) {
        return clientService.addActiveProduct(clientId, activeProductDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping("/{clientId}/passiveProducts")
    public Mono<ResponseEntity<List<PassiveProductDto>>> getPassiveProductsByClient(@PathVariable String clientId) {
        return clientService.getPassiveProductsByClient(clientId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{clientId}/activeProducts")
    public Mono<ResponseEntity<List<ActiveProductDto>>> getActiveProductsByClient(@PathVariable String clientId) {
        return clientService.getActiveProductsByClient(clientId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping("/{clientId}/activeProducts/{id}")
    public Mono<ResponseEntity<ActiveProductDto>> getActiveProductById(@PathVariable String clientId, @PathVariable String id) {
        return clientService.getActiveProductById(clientId, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping("/{clientId}/passiveProducts/{id}")
    public Mono<ResponseEntity<PassiveProductDto>> getPassiveProductById(@PathVariable String clientId, @PathVariable String id) {
        return clientService.getPassiveProductById(clientId, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PutMapping("/{clientId}/passiveProducts/{id}")
    public Mono<ResponseEntity<ClientDto>> updatePassiveProduct(@PathVariable String clientId, @PathVariable String id, @RequestBody PassiveProductDto passiveProductDto) {
        return clientService.updatePassiveProduct(clientId, id, passiveProductDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{clientId}/activeProducts/{id}")
    public Mono<ResponseEntity<ClientDto>> updateActiveProduct(@PathVariable String clientId, @PathVariable String id, @RequestBody ActiveProductDto activeProductDto) {
        return clientService.updateActiveProduct(clientId, id, activeProductDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //Movements
    @PostMapping("/{clientId}/passiveProducts/{id}/deposit")
    public Mono<ResponseEntity<PassiveProductDto>> deposit(@PathVariable String clientId, @PathVariable String id, @RequestBody Double amount) {
        return clientService.deposit(clientId, id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping("/{clientId}/passiveProducts/{id}/withdraw")
    public Mono<ResponseEntity<PassiveProductDto>> withdraw(@PathVariable String clientId, @PathVariable String id, @RequestBody Double amount) {
        return clientService.withdraw(clientId, id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping("/{clientId}/activeProducts/{id}/payment")
    public Mono<ResponseEntity<ActiveProductDto>> makePaymentC(@PathVariable String clientId, @PathVariable String id, @RequestBody double amount) {
        return clientService.makePaymentC(clientId, id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    //New funcionalities
    @GetMapping("/{clientId}/summary")
    public Mono<ClientSummaryDto> getClientSummary(@PathVariable String clientId) {
        return clientService.getClientSummary(clientId);
    }
}

