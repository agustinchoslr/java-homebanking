package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionRepository.findAll().stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/transactions/{id}")
    public TransactionDTO getTransactions(@PathVariable Long id) {
       // return new TransactionDTO(transactionRepository.findById(id).orElse(null));
        return new TransactionDTO(Objects.requireNonNull(transactionRepository.findById(id).orElse(null)));

    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction (
            @RequestParam Double amount, @RequestParam String description, @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, Authentication authentication
    ) {
        Account originAccount = accountRepository.findByNumber(fromAccountNumber);
        Account destinyAccount = accountRepository.findByNumber(toAccountNumber);

        if (amount.isNaN() || amount< 0.0) {
            return new ResponseEntity<>("Please enter a valid amount", HttpStatus.FORBIDDEN);
        }
        if (description.isEmpty()) {
            return new ResponseEntity<>("Please enter the description", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Please enter the origin account", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Please enter the destiny account", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("The accounts are the same", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.isBlank()) {
            return new ResponseEntity<>("The origin account doesn't exist", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber == null) {
            return new ResponseEntity<>("The destination account doesn't exist", HttpStatus.FORBIDDEN);
        }
        if (clientRepository.findByEmail(authentication.getName()).getAccounts().stream().noneMatch(account -> account.getNumber().equals(fromAccountNumber))) {
            return new ResponseEntity<>("The origin account doesn't belong to you", HttpStatus.FORBIDDEN);
        }
        if (amount > originAccount.getBalance()) {
            return new ResponseEntity<>("Insufficient funds", HttpStatus.FORBIDDEN);
        }

        Transaction debit = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now());
        originAccount.addTransaction(debit);
        originAccount.setBalance(originAccount.getBalance() - amount);
        transactionRepository.save(debit);
        accountRepository.save(originAccount);

        Transaction credit = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        destinyAccount.addTransaction(credit);
        destinyAccount.setBalance(destinyAccount.getBalance() + amount);
        transactionRepository.save(credit);
        accountRepository.save(destinyAccount);

        return new ResponseEntity<>("Transaction executed correctly", HttpStatus.CREATED);
    }
}