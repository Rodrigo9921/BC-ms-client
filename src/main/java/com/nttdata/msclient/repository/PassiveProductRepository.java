package com.nttdata.msclient.repository;

import com.nttdata.msclient.model.PassiveProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PassiveProductRepository extends ReactiveMongoRepository<PassiveProduct,String > {
}
