package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reprezentuje standardową talię 52 kart.
 * Odpowiada za utworzenie talii, tasowanie i wydawanie kart.
 */
public class Deck {
    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        initializeFullDeck();
    }

    private void initializeFullDeck() {
        cards.clear();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }

        return cards.remove(0);
    }

    public int remainingCards() {
        return cards.size();
    }
}
