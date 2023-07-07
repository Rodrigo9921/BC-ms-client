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

import java.util.List;
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
    //Passive and Active Products
    @Override
    public Mono<ClientDto> addPassiveProduct(String clientId, PassiveProductDto passiveProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    PassiveProduct newProduct = PassiveProductMapper.convertToEntity(passiveProductDto);
                    return Mono.just(client)
                            .filter(c -> !client.getType().equals("Personal") || client.getPassiveProduct().size() < 3)
                            .switchIfEmpty(Mono.error(new RuntimeException("Personal clients can have up to 3 passive products")))
                            .filter(c -> !client.getType().equals("Business") || newProduct.getName().equals("Current Account"))
                            .switchIfEmpty(Mono.error(new RuntimeException("Business clients can only have Current Accounts")))
                            .filter(c -> client.getPassiveProduct().stream().noneMatch(p -> p.getName().equals(newProduct.getName())))
                            .switchIfEmpty(Mono.error(new RuntimeException("Client already has a product of this type")))
                            .filter(c -> !client.getType().equals("VIP") || newProduct.getName().equals("Savings Account") && client.getActiveProduct().stream().anyMatch(p -> p.getName().equals("Credit Card")))
                            .switchIfEmpty(Mono.error(new RuntimeException("VIP clients must have a credit card to open a savings account")))
                            .filter(c -> !client.getType().equals("PYME") || newProduct.getName().equals("Current Account") || (client.getPassiveProduct().stream().anyMatch(p -> p.getName().equals("Current Account")) && client.getActiveProduct().stream().anyMatch(p -> p.getName().equals("Credit Card"))))
                            .switchIfEmpty(Mono.error(new RuntimeException("PYME clients must have a current account and a credit card to open a new account")))
                            .doOnNext(c -> {
                                client.getPassiveProduct().add(newProduct);
                                passiveProductRepository.save(newProduct).subscribe();
                            })
                            .flatMap(clientRepository::save);
                })
                .map(ClientMapper::convertToDto);
    }


    /*
    @Override
    public Mono<ClientDto> addPassiveProduct(String clientId, PassiveProductDto passiveProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    PassiveProduct newProduct = PassiveProductMapper.convertToEntity(passiveProductDto);
                    return Mono.just(client)
                            .filter(c -> !client.getType().equals("Personal") || client.getPassiveProduct().size() < 3)
                            .switchIfEmpty(Mono.error(new RuntimeException("Personal clients can have up to 3 passive products")))
                            .filter(c -> !client.getType().equals("Business") || newProduct.getName().equals("Current Account"))
                            .switchIfEmpty(Mono.error(new RuntimeException("Business clients can only have Current Accounts")))
                            .filter(c -> client.getPassiveProduct().stream().noneMatch(p -> p.getName().equals(newProduct.getName())))
                            .switchIfEmpty(Mono.error(new RuntimeException("Client already has a product of this type")))
                            .doOnNext(c -> {
                                client.getPassiveProduct().add(newProduct);
                                passiveProductRepository.save(newProduct).subscribe();
                            })
                            .flatMap(clientRepository::save);
                })
                .map(ClientMapper::convertToDto);
    }
    */
    @Override
    public Mono<ClientDto> addActiveProduct(String clientId, ActiveProductDto activeProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    ActiveProduct newProduct = ActiveProductMapper.convertToEntity(activeProductDto);
                    return Mono.just(client)
                            .filter(c -> !client.getType().equals("Personal") || client.getActiveProduct().stream().noneMatch(p -> p.getName().equals("Personal Loan")))
                            .switchIfEmpty(Mono.error(new RuntimeException("Personal clients can only have one Personal Loan")))
                            .doOnNext(c -> {
                                client.getActiveProduct().add(newProduct);
                                activeProductRepository.save(newProduct).subscribe();
                            })
                            .flatMap(clientRepository::save);
                })
                .map(ClientMapper::convertToDto);
    }
    @Override
    public Mono<List<PassiveProductDto>> getPassiveProductsByClient(String clientId) {
        return clientRepository.findById(clientId)
                .flatMap(client -> Flux.fromIterable(client.getPassiveProduct())
                        .map(PassiveProductMapper::convertToDto)
                        .collectList());
    }

    @Override
    public Mono<List<ActiveProductDto>> getActiveProductsByClient(String clientId) {
        return clientRepository.findById(clientId)
                .flatMap(client -> Flux.fromIterable(client.getActiveProduct())
                        .map(ActiveProductMapper::convertToDto)
                        .collectList());
    }
    @Override
    public Mono<ActiveProductDto> getActiveProductById(String clientId, String id) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    ActiveProduct product = client.getActiveProduct().stream()
                            .filter(p -> p.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    return Mono.just(ActiveProductMapper.convertToDto(product));
                });
    }
    @Override
    public Mono<PassiveProductDto> getPassiveProductById(String clientId, String productId) {
        return clientRepository.findById(clientId)
                .flatMap(client -> client.getPassiveProduct().stream()
                        .filter(product -> product.getId().equals(productId))
                        .findFirst()
                        .map(Mono::just)
                        .orElseGet(Mono::empty))
                .map(PassiveProductMapper::convertToDto);
    }
    @Override
    public Mono<ClientDto> updatePassiveProduct(String clientId, String id, PassiveProductDto passiveProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    PassiveProduct updatedProduct = PassiveProductMapper.convertToEntity(passiveProductDto);
                    client.getPassiveProduct().removeIf(p -> p.getId().equals(id));
                    client.getPassiveProduct().add(updatedProduct);
                    return clientRepository.save(client);
                })
                .map(ClientMapper::convertToDto);
    }

    @Override
    public Mono<ClientDto> updateActiveProduct(String clientId, String id, ActiveProductDto activeProductDto) {
        return clientRepository.findById(clientId)
                .flatMap(client -> {
                    ActiveProduct updatedProduct = ActiveProductMapper.convertToEntity(activeProductDto);
                    client.getActiveProduct().removeIf(p -> p.getId().equals(id));
                    client.getActiveProduct().add(updatedProduct);
                    return clientRepository.save(client);
                })
                .map(ClientMapper::convertToDto);
    }

    //Movements
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
                            .filter(p -> !"Savings Account".equals(p.getName()) || p.getMonthlyMovementLimit() > 0)
                            .switchIfEmpty(Mono.error(new RuntimeException("Monthly movement limit reached")))
                            .doOnNext(p -> {
                                p.setBalance(p.getBalance() + amount);
                                if ("Savings Account".equals(p.getName())) {
                                    p.setMonthlyMovementLimit(p.getMonthlyMovementLimit() - 1);
                                }
                            })
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
                    PassiveProduct product = client.getPassiveProduct().stream()
                            .filter(p -> p.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    return Mono.just(product)
                            .filter(p -> amount >= 0)
                            .switchIfEmpty(Mono.error(new RuntimeException("Withdrawal amount must be positive")))
                            .filter(p -> p.getBalance() >= amount)
                            .switchIfEmpty(Mono.error(new RuntimeException("Insufficient balance")))
                            .filter(p -> !"Current Account".equals(p.getName()) || p.getBalance() >= amount + amount * 0.01)
                            .switchIfEmpty(Mono.error(new RuntimeException("Insufficient balance to cover commission")))
                            .doOnNext(p -> {
                                double commission = "Current Account".equals(p.getName()) ? amount * 0.01 : 0;
                                p.setBalance(p.getBalance() - amount - commission);
                            })
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
    public Mono<ActiveProductDto> makePaymentC(String clientId, String id, double amount) {
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

}

