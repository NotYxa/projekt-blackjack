package com.blackjack.logic;

import com.blackjack.model.Dealer;
import com.blackjack.model.Deck;
import com.blackjack.model.Player;

/**
 * Główny silnik gry zawiadujący przebiegiem rundy.
 */
public class BlackjackGame {
    private final Deck deck;
    private final Player player;
    private final Dealer dealer;
    private final HandEvaluator evaluator;

    public BlackjackGame() {
        this.player = new Player();
        this.dealer = new Dealer();
        this.evaluator = new HandEvaluator();
        
        // Kasynowy but z kartami (Shoe) - 6 talii
        this.deck = new Deck(6);
        this.deck.shuffle();
    }

    /**
     * Rozpoczyna nową rundę gry.
     */
    public void startRound() {
        // 1. Sprawdzamy czy but nie jest pusty. Wracamy do standardowego 75 kart.
        if (deck.remainingCards() < 75) {
            deck.reset();
            deck.shuffle();
        }

        // 2. Czyścimy stoły (ręce) gracza i krupiera
        player.reset();
        dealer.reset();

        // 3. Rozdajemy po dwie karty (naprzemiennie, jak u prawdziwego krupiera)
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());
    }

    /**
     * Gracz dobiera kartę (tzw. HIT).
     */
    public void playerHit() {
        player.getHand().addCard(deck.dealCard());
    }

    /**
     * Sprawdza, czy gracz przekroczył 21 (tzw. BUST - "fura").
     */
    public boolean isPlayerBusted() {
        return evaluator.calculateValue(player.getHand()) > 21;
    }

    /**
     * Tura Krupiera po tym, jak gracz zakończy dobierać (STAND).
     * Złota zasada z kasyna: Krupier dobiera, dopóki ma mniej niż 17 punktów.
     */
    public void playDealerTurn() {
        while (evaluator.calculateValue(dealer.getHand()) < 17) {
            dealer.getHand().addCard(deck.dealCard());
        }
    }

    /**
     * Zwraca wynik aktualnej gry, po tym jak obaj gracze skończyli ruchy.
     */
    public GameResult determineWinner() {
        int playerScore = evaluator.calculateValue(player.getHand());
        int dealerScore = evaluator.calculateValue(dealer.getHand());

        if (playerScore > 21) {
            return GameResult.DEALER_WINS; // Gracz wyleciał
        }
        if (dealerScore > 21) {
            return GameResult.PLAYER_WINS; // Krupier wyleciał
        }

        // Kto ma więcej od drugiego, ale wciąż do 21
        if (playerScore > dealerScore) {
            return GameResult.PLAYER_WINS;
        } else if (dealerScore > playerScore) {
            return GameResult.DEALER_WINS;
        } else {
            return GameResult.PUSH; // Tyle samo punktów = remis
        }
    }

    // Gettery żeby interfejs (UI) miał jak podejrzeć obecny stan
    public Player getPlayer() { return player; }
    public Dealer getDealer() { return dealer; }
    public HandEvaluator getEvaluator() { return evaluator; }
    public Deck getDeck() { return deck; }
}