package de.mschoettle.entity.repository;

import de.mschoettle.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IAccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByName(String name);

    Optional<Account> findByEmail(String email);
}
