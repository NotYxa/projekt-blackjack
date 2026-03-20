package com.blackjack.logic;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import com.blackjack.model.Rank;

/**
 * Odpowiada wyłącznie za ocenę punktową ręki uwzględniając regułę Asów.
 */
public class HandEvaluator {

    /**
     * Liczy optymalną wartość ręki.
     * Dodaje karty. Jeśli As przekroczy 21, cofa jego wartość z 11 na 1.
     */
    public int calculateValue(Hand hand) {
        int total = 0;
        int acesCount = 0;

        for (Card card : hand.getCards()) {
            total += card.getValue(); // To domyślnie dodaje Asa jako 11 (masz tak w Rank)
            
            if (card.getRank() == Rank.ACE) {
                acesCount++;
            }
        }

        // Dopóki nasz wynik to "busta" (powyżej 21) i wciąż mamy "duże" asy do zmiany
        while (total > 21 && acesCount > 0) {
            total -= 10;   
            acesCount--;   
        }

        return total;
    }
}