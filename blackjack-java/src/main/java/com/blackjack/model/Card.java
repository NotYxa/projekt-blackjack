package com.blackjack.model;

import java.util.Objects;

/**
 * Reprezentuje pojedynczą kartę: wartość (Rank) i kolor (Suit).
 * Ta klasa jest niemutowalna (immutable) - po utworzeniu karty jej pola się nie zmieniają.
 */
public class Card {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = Objects.requireNonNull(rank, "rank cannot be null");
        this.suit = Objects.requireNonNull(suit, "suit cannot be null");
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank.getDisplayName() + " of " + suit;
    }
}
