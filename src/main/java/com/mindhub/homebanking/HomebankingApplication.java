package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

@Bean
	public CommandLineRunner init(ClientRepository clientRepository, AccountRepository accountRepository){
		return args -> {
			Client melba = new Client("Melba", "Morel", "melmor@email.com");
			Client agustin = new Client("Agustin", "Juan", "agustin@email.com");

			clientRepository.save(melba);
			clientRepository.save(agustin);

			/*accountRepository.save(new Account(melba, LocalDate.now(), 5000.00));
			accountRepository.save(new Account(agustin, LocalDate.now().plusDays(1), 7500.00));
			*/
			Account account1 = new Account(7500.0, LocalDate.now().plusDays(1), "VIN001");
			Account account2 = new Account( 5000.0, LocalDate.now(), "VIN002");
			melba.addAccount(account1);
			melba.addAccount(account2);
			accountRepository.save(account1);
			accountRepository.save(account2);
		};
	}

}
