package com.blackjack.model;

/**
 * Reprezentuje gracza uczestniczącego w grze.
 */
public class Player {
    private final Hand hand;

    public Player() {
        this.hand = new Hand();

    }

    public Hand getHand() {
        return hand;
    }

    public void reset() {
        hand.clear();
    }
}
