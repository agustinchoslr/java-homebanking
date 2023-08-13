package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

@Bean
public CommandLineRunner init(ClientRepository clientRepository,
							  AccountRepository accountRepository,
							  TransactionRepository transactionRepository,
							  LoanRepository loanRepository,
							  ClientLoanRepository clientLoanRepository){
	return args -> {
		Client client1 = new Client("Melba", "Morel", "melmor@email.com");
		Client client2 = new Client("Agustin", "Juan", "agustin@email.com");

		clientRepository.save(client1);
		clientRepository.save(client2);

            /*accountRepository.save(new Account(melba, LocalDate.now(), 5000.00));
			accountRepository.save(new Account(agustin, LocalDate.now().plusDays(1), 7500.00));
			*/
		Account account1 = new Account(7500.0, LocalDate.now().plusDays(1), "VIN001");
		Account account2 = new Account( 5000.0, LocalDate.now(), "VIN002");
		Account account3 = new Account(6000.0, LocalDate.now().minusDays(2),"VIN003");

		client1.addAccount(account1);
		client1.addAccount(account2);
		client2.addAccount(account3);

		accountRepository.save(account1);
		accountRepository.save(account2);
		accountRepository.save(account3);


//			transactions
		Transaction transaction1 = new Transaction(TransactionType.DEBIT, -1000.0, "Compra en comercio", LocalDateTime.now());
		account1.addTransaction(transaction1);
		transactionRepository.save(transaction1);
		Transaction transaction2 = new Transaction(TransactionType.CREDIT, 500.0, "Reintegro promocional verano", LocalDateTime.now());
		account1.addTransaction(transaction2);
		transactionRepository.save(transaction2);
		Transaction transaction3 = new Transaction(TransactionType.DEBIT, -1000.0, "Compra en comercio", LocalDateTime.now());
		account2.addTransaction(transaction3);
		transactionRepository.save(transaction3);
		Transaction transaction4 = new Transaction(TransactionType.CREDIT, 500.0, "Transferencia recibida", LocalDateTime.now());
		account2.addTransaction(transaction4);
		transactionRepository.save(transaction4);

		Loan loan1 = new Loan("Hipotecario", 500000, Set.of(12, 24, 36, 48, 60));
		Loan loan2 = new Loan("Personal", 100000, Set.of(6, 12, 24));
		Loan loan3 = new Loan("Automotriz", 300000, Set.of(6, 12, 24, 36));

		ClientLoan clientLoan1 = new ClientLoan(400000, 60, client1, loan1);
		ClientLoan clientLoan2 = new ClientLoan(50000, 12, client1, loan2);
		ClientLoan clientLoan3 = new ClientLoan(100000, 24, client2, loan2);
		ClientLoan clientLoan4 = new ClientLoan(200000, 36, client2, loan3);

		loanRepository.save(loan1);
		loanRepository.save(loan2);
		loanRepository.save(loan3);

		clientLoanRepository.save(clientLoan1);
		clientLoanRepository.save(clientLoan2);
		clientLoanRepository.save(clientLoan3);
		clientLoanRepository.save(clientLoan4);
	};
}

}
