/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.ndportmann.mdc_webflux.service.model.Foo;

/**
 * @author Gbenga
 *
 */
public interface IFooDao extends MongoRepository<Foo, Long> {
    
}