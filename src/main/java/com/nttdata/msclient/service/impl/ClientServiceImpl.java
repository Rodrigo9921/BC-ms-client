package com.nttdata.msclient.service.impl;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.dto.PassiveProductDto;
import com.nttdata.msclient.model.ActiveProduct;
import com.nttdata.msclient.model.PassiveProduct;
import com.nttdata.msclient.repository.ActiveProductRepository;
import com.nttdata.msclient.repository.ClientRepository;
import com.nttdata.msclient.repository.PassiveProductRepository;
import com.nttdata.msclient.service.ClientService;
import com.nttdata.msclient.utils.ActiveProductMapper;
import com.nttdata.msclient.utils.ClientMapper;
import com.nttdata.msclient.utils.PassiveProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PassiveProductRepository passiveProductRepository;
    private final ActiveProductRepository activeProductRepository;
    private final WebClient webClient;

    public ClientServiceImpl(ClientRepository clientRepository,PassiveProductRepository passiveProductRepository,ActiveProductRepository activeProductRepository, WebClient.Builder webClientBuilder) {
        this.clientRepository = clientRepository;
        this.passiveProductRepository = passiveProductRepository;
        this.activeProductRepository= activeProductRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082/transactions").build(); // replace with the actual URL of your ms-transaccions service
    }

    @Override
    public Flux<ClientDto> getAllClients() {
        return clientRepository.findAll().map(ClientMapper::convertToDto);
    }

    @Override
    public Mono<ClientDto> getClientById(String clientId) {
        return clientRepository.findById(clientId).map(ClientMapper::convertToDto);
    }

    @Override
    public Mono<ClientDto> createClient(ClientDto clientDto) {
        return clientRepository.save(ClientMapper.convertToEntity(clientDto)).map(ClientMapper::convertToDto);
    }

    @Override
    public Mono<ClientDto> updateClient(String clientId, ClientDto clientDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    client.setName(clientDto.getName());
                    client.setLastname(clientDto.getLastname());
                    client.setDni(clientDto.getDni());
                    client.setPhone(clientDto.getPhone());
                    client.setEmail(clientDto.getEmail());
                    client.setAddress(clientDto.getAddress());
                    client.setType(clientDto.getType());
                    client.setPassiveProduct(clientDto.getPassiveProducts().stream().map(PassiveProductMapper::convertToEntity).collect(Collectors.toList()));
                    client.setActiveProduct(clientDto.getActiveProducts().stream().map(ActiveProductMapper::convertToEntity).collect(Collectors.toList()));
                    return clientRepository.save(client);
                })
                .map(ClientMapper::convertToDto);
    }

    @Override
    public Mono<Void> deleteClient(String clientId) {
        return clientRepository.deleteById(clientId);
    }

    @Override
    public Mono<ClientDto> addPassiveProduct(String clientId, PassiveProductDto passiveProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    if (client.getPassiveProduct().stream().anyMatch(product -> product.getId().equals(passiveProductDto.getId()))) {
                        return Mono.error(new DuplicateProductException("The product has been previously registered."));
                    }
                    client.getPassiveProduct().add(PassiveProductMapper.convertToEntity(passiveProductDto));
                    return clientRepository.save(client);
                })
                .map(ClientMapper::convertToDto);
    }

    @Override
    public Mono<ClientDto> addActiveProduct(String clientId, ActiveProductDto activeProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    if (client.getActiveProduct().stream().anyMatch(product -> product.getId().equals(activeProductDto.getId()))) {
                        return Mono.error(new DuplicateProductException("The product has been previously registered."));
                    }
                    client.getActiveProduct().add(ActiveProductMapper.convertToEntity(activeProductDto));
                    return clientRepository.save(client);
                })
                .map(ClientMapper::convertToDto);
    }

    @Override
    public Flux<PassiveProductDto> getPassiveProductsByClient(String clientId) {
        return clientRepository.findById(clientId)
                .flatMapMany(client -> Flux.fromIterable(client.getPassiveProduct()))
                .map(PassiveProductMapper::convertToDto);
    }

    @Override
    public Flux<ActiveProductDto> getActiveProductsByClient(String clientId) {
        return clientRepository.findById(clientId)
                .flatMapMany(client -> Flux.fromIterable(client.getActiveProduct()))
                .map(ActiveProductMapper::convertToDto);
    }

    @Override
    public Mono<PassiveProductDto> deposit(String clientId, String id, Double amount) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    PassiveProduct product = client.getPassiveProduct().stream()
                            .filter(p -> p.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    return Mono.just(product)
                            .filter(p -> amount >= 0)
                            .switchIfEmpty(Mono.error(new RuntimeException("Deposit amount must be positive")))
                            .doOnNext(p -> p.setBalance(p.getBalance() + amount))
                            .flatMap(passiveProductRepository::save)
                            .doOnSuccess(p -> {
                                client.getPassiveProduct().removeIf(prod -> prod.getId().equals(p.getId()));
                                client.getPassiveProduct().add(p);
                                clientRepository.save(client).subscribe();
                            });
                })
                .map(PassiveProductMapper::convertToDto);
    }

    @Override
    public Mono<PassiveProductDto> withdraw(String clientId, String id, Double amount) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    PassiveProduct passiveProduct = client.getPassiveProduct().stream()
                            .filter(p -> p.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    return Mono.just(passiveProduct)
                            .filter(p -> amount >= 0)
                            .switchIfEmpty(Mono.error(new RuntimeException("Withdrawal amount must be positive")))
                            .filter(p -> p.getBalance() >= amount)
                            .switchIfEmpty(Mono.error(new RuntimeException("Insufficient balance")))
                            .doOnNext(p -> p.setBalance(p.getBalance() - amount))
                            .flatMap(passiveProductRepository::save)
                            .doOnSuccess(p -> {
                                client.getPassiveProduct().removeIf(prod -> prod.getId().equals(p.getId()));
                                client.getPassiveProduct().add(p);
                                clientRepository.save(client).subscribe();
                            });
                })
                .map(PassiveProductMapper::convertToDto);
    }
    @Override
    public Mono<ActiveProductDto> makePayment(String clientId, String id, double amount) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    ActiveProduct product = client.getActiveProduct().stream()
                            .filter(p -> p.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    if (product.getBalance() < amount) {
                        throw new RuntimeException("Insufficient balance");
                    }
                    product.setBalance(product.getBalance() - amount);
                    return activeProductRepository.save(product)
                            .doOnSuccess(p -> {
                                client.getActiveProduct().removeIf(prod -> prod.getId().equals(p.getId()));
                                client.getActiveProduct().add(p);
                                clientRepository.save(client).subscribe();
                            });
                })
                .map(ActiveProductMapper::convertToDto);
    }

    private class DuplicateProductException extends RuntimeException{
        public DuplicateProductException(String message) {
            super(message);
        }
    }
}

