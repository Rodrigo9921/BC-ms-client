package com.nttdata.msclient.controller;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.dto.PassiveProductDto;
import com.nttdata.msclient.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<PassiveProductDto> getPassiveProductsByClient(@PathVariable String clientId) {
        return clientService.getPassiveProductsByClient(clientId);
    }

    @GetMapping("/{clientId}/activeProducts")
    public Flux<ActiveProductDto> getActiveProductsByClient(@PathVariable String clientId) {
        return clientService.getActiveProductsByClient(clientId);
    }
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
    public Mono<ResponseEntity<ActiveProductDto>> makePayment(@PathVariable String clientId, @PathVariable String id, @RequestBody double amount) {
        return clientService.makePayment(clientId, id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}

