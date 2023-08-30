package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toSet());
    }
    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }
    @GetMapping("/clients/current/accounts")
    public Set<AccountDTO> getCurrentAccounts(Authentication authentication)
    {
        Client client = clientRepository.findByEmail(authentication.getName());
        return client.getAccounts()
                .stream()
                .map(AccountDTO::new)
                .collect(Collectors.toSet());
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication)
    {
        Client client = clientRepository.findByEmail(authentication.getName());

        if (client.getAccounts().size() < 3) {
            String number;
            boolean check;
            do{
                number = Utils.generateAccountNumber();
                check = accountRepository.existsByNumber(number);
            }while(check);
            Account account= new Account(0.0, LocalDate.now(), number, client);

            accountRepository.save(account);
            return new ResponseEntity<>("Account added to client " + account.getClientOwner().getEmail() , HttpStatus.CREATED);

        }

        return new ResponseEntity<>("The Maximum Number of Accounts has been reached", HttpStatus.FORBIDDEN);
    }

    @GetMapping("clients/current/accounts/{id}")
    public AccountDTO getCurrentAccount(Authentication authentication, @PathVariable Long id){

        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findById(id).orElse(null);
        if ((account != null) && (client.getAccounts().contains(account))){
            return new AccountDTO(account);
        }
        return null;
    }

}
