package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDTO {

    // atributos
    private long id;
    private String number;
    private LocalDate creationDate;
    private double balance;

    private Set<TransactionDTO> transactions = new HashSet<>();
    //account's owner
    // private Client clientOwner;

    // constructor
    public AccountDTO(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.creationDate = account.getCreationDate();
        this.balance = account.getBalance();
        this.transactions = account.getTransactions().stream().map(transaction -> new TransactionDTO(transaction)).collect(Collectors.toSet());
    }


    // getters
    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public double getBalance() {
        return balance;
    }


    public Set<TransactionDTO> getTransactions() {
        return transactions;
    }

   /* public Client getClientOwner() {
        return clientOwner;
    }*/
}
