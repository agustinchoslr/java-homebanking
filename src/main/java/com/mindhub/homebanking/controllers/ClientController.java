package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientController;
    private AccountRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientController.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String firstName,
                                           @RequestParam String lastName,
                                           @RequestParam String email,
                                           @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (clientController.findByEmail(email) != null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

       Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        String number;
        boolean check;
        do{
            number = Utils.generateAccountNumber();
            check = repo.existsByNumber(number);
        }while(check);
        Account account = new Account(0.0, LocalDate.now(),number);

        repo.save(account);
        client.addAccount(account);
        clientController.save(client);

        return new ResponseEntity<>("Client " + client.getEmail() + " was created",HttpStatus.CREATED);

    }


    @GetMapping("clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientController.findById(id).map(ClientDTO::new).orElse(null);
    }

    @GetMapping("/clients/current")
    public ClientDTO getCurrent(Authentication authentication) {
        Client client =  clientController.findByEmail(authentication.getName());

        return new ClientDTO(client);
    }


}
