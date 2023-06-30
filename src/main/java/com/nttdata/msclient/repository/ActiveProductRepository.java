package com.nttdata.msclient.repository;


import com.nttdata.msclient.model.ActiveProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ActiveProductRepository extends ReactiveMongoRepository<ActiveProduct, String > {
}
