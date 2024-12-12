package com.tripactions.infra.mysql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripactions.infra.mysql.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {}