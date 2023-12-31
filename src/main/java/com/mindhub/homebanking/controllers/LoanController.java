package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(
            @RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication)
    {
        Client client = clientRepository.findByEmail(authentication.getName());
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);
        Account destinyAccount = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());


        if (loanApplicationDTO.getAmount() <= 0.0 || loanApplicationDTO.getPayments() <= 0) {
            return new ResponseEntity<>("Missing parameters", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getLoanId() == null) {
            return new ResponseEntity<>("Loan doesn't exist", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("Amount requested exceeds the maximum of the loan", HttpStatus.FORBIDDEN);
        }
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("Payments requested exceeds the maximum of the loan", HttpStatus.FORBIDDEN);
        }
        if (!accountRepository.existsByNumber(destinyAccount.getNumber())) {
         //   System.out.println("destinyAccount = " + loanApplicationDTO.getDestinationAccount());
            return new ResponseEntity<>("Destiny account doesn't exist", HttpStatus.FORBIDDEN);
        }
        if (!client.getAccounts().contains(destinyAccount)) {
            return new ResponseEntity<>("The account doesn't correspond to the client", HttpStatus.FORBIDDEN);
        }

        double loanRevenue = (loanApplicationDTO.getAmount() * 1.20);

        ClientLoan newClientLoan = new ClientLoan(loanRevenue,loanApplicationDTO.getPayments());
        Transaction newTransaction = new Transaction(TransactionType.CREDIT, loanRevenue, loan.getName() + " - Loan approved", LocalDateTime.now());
        destinyAccount.setBalance(destinyAccount.getBalance() + loanApplicationDTO.getAmount());
        destinyAccount.addTransaction(newTransaction);

        loan.addClientLoan(newClientLoan);
        client.addClientLoan(newClientLoan);
        clientLoanRepository.save(newClientLoan);
        transactionRepository.save(newTransaction);
        accountRepository.save(destinyAccount);
      //  loanRepository.save(loan);
     //   clientRepository.save(client);

        return new ResponseEntity<>("Loan applied correctly", HttpStatus.CREATED);
    }
}