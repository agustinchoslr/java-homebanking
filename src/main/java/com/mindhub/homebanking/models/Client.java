package com.mindhub.homebanking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Client {
@Id
@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
@GenericGenerator(name = "native", strategy = "native")
private Long id;

    private String firstName;
    private String lastName;

    private String email;
    @OneToMany(mappedBy = "clientCardHolder", fetch = FetchType.EAGER)
    private Set<Card> cards = new HashSet<>();

    @OneToMany(mappedBy="clientOwner", fetch= FetchType.EAGER)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<ClientLoan> clientLoans = new HashSet<>();

    public Client() { }

    public Client(String first, String last, String mail) {
        firstName = first;
        lastName = last;
        email = mail;
    }



    public Set<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        account.setClientOwner(this);
        accounts.add(account);
    }


    public void addClientLoan(ClientLoan clientLoan) {
        clientLoan.setClient(this);
        clientLoans.add(clientLoan);
    }

    public Set<ClientLoan> getClientLoans() {
        return this.clientLoans;
    }

    @JsonIgnore
    public List<Loan> getLoans() {
        return clientLoans.stream().map(ClientLoan::getLoan).collect(Collectors.toList());
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return firstName + " " + lastName;
    }

    public Long getId() { return id;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        card.setClientCardHolder(this);
        cards.add(card);
    }

}
