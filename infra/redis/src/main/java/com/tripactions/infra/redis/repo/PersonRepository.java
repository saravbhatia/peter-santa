package com.tripactions.infra.redis.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tripactions.infra.redis.model.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, String> {}
