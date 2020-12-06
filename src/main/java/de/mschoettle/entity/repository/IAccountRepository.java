package de.mschoettle.entity.repository;

import de.mschoettle.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IAccountRepository extends CrudRepository<Account, Long> {

    Account findByName(String name);

    Account findByEmail(String email);
}
