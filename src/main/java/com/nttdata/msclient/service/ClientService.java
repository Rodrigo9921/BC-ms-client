package com.nttdata.msclient.service;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.dto.PassiveProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientService {
    Flux<ClientDto> getAllClients();
    Mono<ClientDto> getClientById(String clientId);
    Mono<ClientDto> createClient(ClientDto clientDto);
    Mono<ClientDto> updateClient(String clientId, ClientDto clientDto);
    Mono<Void> deleteClient(String clientId);
    Mono<ClientDto> addPassiveProduct(String clientId, PassiveProductDto passiveProductDto);
    Mono<ClientDto> addActiveProduct(String clientId, ActiveProductDto activeProductDto);
    Flux<PassiveProductDto> getPassiveProductsByClient(String clientId);
    Flux<ActiveProductDto> getActiveProductsByClient(String clientId);
    Mono<PassiveProductDto> deposit(String clientId, String id, Double amount);
    Mono<PassiveProductDto> withdraw(String clientId, String id, Double amount);

    Mono<ActiveProductDto> makePayment(String clientId, String id, double amount);

}
