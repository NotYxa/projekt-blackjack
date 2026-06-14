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
    private final int numberOfDecks;
    private int runningCountHiLo; // Hi-Lo licznik
    private int runningCountZen; // Zen Count licznik

    public Deck(int numberOfDecks) {
        this.cards = new ArrayList<>();
        this.numberOfDecks = numberOfDecks;
        this.runningCountHiLo = 0;
        this.runningCountZen = 0;
        reset();
    }

    public void reset() {
        cards.clear();
        runningCountHiLo = 0; // Resetujemy licznik przy tasowaniu
        runningCountZen = 0;

        for (int i = 0; i < numberOfDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(rank, suit));
                }
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

        Card dealt = cards.remove(0);
        updateRunningCount(dealt);
        return dealt;
    }

    private void updateRunningCount(Card card) {
        int val = card.getRank().getValue();
        // Hi-Lo
        if (val >= 2 && val <= 6) {
            runningCountHiLo += 1;
        } else if (val >= 10 || card.getRank() == Rank.ACE) {
            runningCountHiLo -= 1;
        }

        // Zen Count
        switch (card.getRank()) {
            case TWO: case THREE: case SEVEN:
                runningCountZen += 1;
                break;
            case FOUR: case FIVE: case SIX:
                runningCountZen += 2;
                break;
            case TEN: case JACK: case QUEEN: case KING:
                runningCountZen -= 2;
                break;
            case ACE:
                runningCountZen -= 1;
                break;
            default:
                break;
        }
    }

    public int getRunningCountHiLo() {
        return runningCountHiLo;
    }

    public int getRunningCountZen() {
        return runningCountZen;
    }

    public double getTrueCount() {
        double remainingDecks = cards.size() / 52.0;
        if (remainingDecks == 0) return runningCountHiLo;
        return runningCountHiLo / remainingDecks;
    }

    public double getZenTrueCount() {
        double remainingDecks = cards.size() / 52.0;
        if (remainingDecks == 0) return runningCountZen;
        return runningCountZen / remainingDecks;
    }

    public int remainingCards() {
        return cards.size();
    }
}
