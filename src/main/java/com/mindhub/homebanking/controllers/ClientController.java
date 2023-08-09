package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientController;
    private AccountRepository repo;

    @RequestMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientController.findAll().stream().map(ClientDTO::new).collect(toList());
    }


    @RequestMapping("clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientController.findById(id).map(ClientDTO::new).orElse(null);
    }

}
