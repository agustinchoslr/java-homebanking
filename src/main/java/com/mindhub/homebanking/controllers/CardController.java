package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

import static com.mindhub.homebanking.models.CardColor.GOLD;
import static com.mindhub.homebanking.models.CardColor.SILVER;
import static com.mindhub.homebanking.models.CardType.DEBIT;

@RequestMapping( "/api")
@RestController
public class CardController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType cardType, @RequestParam CardColor cardColor) {

        Client clientLogged = clientRepository.findByEmail(authentication.getName());

        ClientDTO clientDto = new ClientDTO( clientLogged );
        Set<CardDTO> cards= clientDto.getCards();

        int foundDebit=0;
        int foundCredit=0;

        for (CardDTO card : cards) {
            if ( cardType == card.getType() ) {
                if ( cardType.equals(DEBIT) ) {
                    foundDebit++;
                }else {
                    foundCredit++;
                }
                if (cardColor == card.getColor()) {
                    if (card.getColor().equals(SILVER)) {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    } else if (card.getColor().equals(GOLD)) {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
        }
        if(foundDebit>=3){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (foundCredit>=3) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Card newCard = new Card( );
        newCard.setCardHolder( clientLogged.getFirstName()+" "+clientLogged.getLastName());
        newCard.setType( cardType );
        newCard.setColor( cardColor );
        newCard.setNumber( new Random().nextInt(9999-1)+1+"-" + new Random().nextInt(9999-1)+1+"-"+ new Random().nextInt(9999-1)+1+"-"+ new Random().nextInt(9999-1)+1 );
        newCard.setCvv(new Random().nextInt(999-1)+1);
        newCard.setFromDate(LocalDate.now() );
        newCard.setThruDate(LocalDate.now().plusYears(5) );

        clientLogged.addCard( newCard );
        cardRepository.save( newCard );

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}