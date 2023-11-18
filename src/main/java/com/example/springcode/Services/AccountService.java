package com.example.springcode.Services;

import com.example.springcode.Entities.Account;
import com.example.springcode.Repositotry.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;
    public List<Account> getListClients(){
        return accountRepository.findAll();
    }
}
