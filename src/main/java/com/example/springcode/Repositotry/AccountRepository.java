package com.example.springcode.Repositotry;

import com.example.springcode.Entities.Account;
import com.example.springcode.Entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByProfileId(Long id);
}
