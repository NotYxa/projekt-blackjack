package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reprezentuje rękę gracza lub krupiera.
 * W tej wersji trzymamy tylko listę kart i prostą sumę wartości.
 * Obsługa Asa jako 1 lub 11 będzie dodana później w logice gry.
 */
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int getSimpleTotal() {
        int sum = 0;

        for (Card card : cards) {
            sum += card.getValue();
        }

        return sum;
    }

    public void clear() {
        cards.clear();
    }
}
