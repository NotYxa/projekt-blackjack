package com.blackjack.model;

/**
 * Reprezentuje gracza uczestniczącego w grze.
 */
public class Player {
    private Hand hand;

    public Player() {
        this.hand = new Hand();

    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public void reset() {
        hand = new Hand();
    }
}
