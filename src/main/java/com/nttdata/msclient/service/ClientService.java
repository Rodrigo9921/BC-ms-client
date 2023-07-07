package com.nttdata.msclient.service;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.dto.ClientSummaryDto;
import com.nttdata.msclient.dto.PassiveProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClientService {
    //Clients
    Flux<ClientDto> getAllClients();
    Mono<ClientDto> getClientById(String clientId);
    Mono<ClientDto> createClient(ClientDto clientDto);
    Mono<ClientDto> updateClient(String clientId, ClientDto clientDto);
    Mono<Void> deleteClient(String clientId);
    //Active and Passive Products
    Mono<ClientDto> addPassiveProduct(String clientId, PassiveProductDto passiveProductDto);
    Mono<ClientDto> addActiveProduct(String clientId, ActiveProductDto activeProductDto);
    Mono<List<PassiveProductDto>> getPassiveProductsByClient(String clientId);
    Mono<List<ActiveProductDto>> getActiveProductsByClient(String clientId);
    Mono<ActiveProductDto> getActiveProductById(String clientId, String id);
    Mono<PassiveProductDto> getPassiveProductById(String clientId, String id);
    Mono<ClientDto> updatePassiveProduct(String clientId, String id, PassiveProductDto passiveProductDto);
    Mono<ClientDto> updateActiveProduct(String clientId, String id, ActiveProductDto activeProductDto);

    //Movements
    Mono<PassiveProductDto> deposit(String clientId, String id, Double amount);
    Mono<PassiveProductDto> withdraw(String clientId, String id, Double amount);
    Mono<ActiveProductDto> makePaymentC(String clientId, String id, double amount);

    //New funcionalities
    Mono<ClientSummaryDto> getClientSummary(String clientId);
}
