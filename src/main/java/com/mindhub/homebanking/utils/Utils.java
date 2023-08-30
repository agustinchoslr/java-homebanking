package com.mindhub.homebanking.utils;

public class Utils {

    static public String generateAccountNumber()
    {
        return "VIN-" + String.format("%08d", 11111111 + (int)(Math.random() * 88888888));
    }

    static public String generateCardNumber()
    {
        String[] cardNumber = new String[4];
        StringBuilder number = new StringBuilder();
        for(int i=0;i<4;i++){
            cardNumber[i] = String.format("%04d", 1000 + (int)(Math.random() * 8999));
            number.append(cardNumber[i]);
            if (i<=2) number.append("-");
        }
        return number.toString();
    }
}


