package com.nttdata.msclient.repository;

import com.nttdata.msclient.model.Client;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ClientRepository extends ReactiveMongoRepository<Client,String> {
    Flux<Client> findByType(String type);
}
