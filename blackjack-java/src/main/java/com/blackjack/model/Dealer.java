package com.blackjack.model;

/**
 * Reprezentuje krupiera. Zasadniczo jest bardzo podobny do gracza,
 * ale rozdzielamy te klasy dla czytelności i przyszłej rozbudowy.
 */
public class Dealer {
    private final Hand hand;

    public Dealer() {
        this.hand = new Hand();
    }

    public Hand getHand() {
        return hand;
    }

    public void reset() {
        hand.clear();
    }
}
