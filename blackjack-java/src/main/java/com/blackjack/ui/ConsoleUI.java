package com.blackjack.ui;

import java.util.List;
import java.util.Scanner;

import com.blackjack.export.CsvExporter;
import com.blackjack.export.GameRecord;
import com.blackjack.logic.BlackjackGame;
import com.blackjack.logic.GameResult;
import com.blackjack.model.Card;

public class ConsoleUI {
    private final BlackjackGame game;
    private final Scanner scanner;
    private final CsvExporter exporter;

    public ConsoleUI() {
        this.game = new BlackjackGame();
        this.scanner = new Scanner(System.in);
        this.exporter = new CsvExporter("C:\\Users\\Dell\\Documents\\GitHub\\projekt-blackjack\\data-output\\results.csv");
    }

    public void start() {
        System.out.println("Witaj w grze Blackjack!");
        boolean playAgain = true;

        while (playAgain) {
            playRound();
            playAgain = askPlayAgain();
        }

        System.out.println("Dzięki za grę!");
    }

    private void playRound() {
        System.out.println("\n--- NOWE ROZDANIE ---");
        game.startRound();

        // Podstawowy przepływ: pokazujemy stół i ruszamy gracza
        printTableInfo(true);
        playerTurn();

        // Jeśli gracz przepali limit (fura!), gra dalej się toczyła nie będzie
        if (!game.isPlayerBusted()) {
            System.out.println("\nTura krupiera...");
            game.playDealerTurn();
            printTableInfo(false); // Odkrywamy karty krupiera
        } else {
            System.out.println("\nPrzekraczasz 21!");
        }

        announceWinner();
    }

    private void playerTurn() {
        while (!game.isPlayerBusted()) {
            System.out.print("\nChcesz dobrać kratę? (H - Hit, S - Stand): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("H")) {
                game.playerHit();
                printTableInfo(true);
            } else if (input.equals("S")) {
                break;
            } else {
                System.out.println("Nieprawidłowy wybór. Wpisz H albo S.");
            }
        }
    }

    private void printTableInfo(boolean hideDealerSecondCard) {
        System.out.println("\n=== STÓŁ ===");

        // Karty krupiera
        List<Card> dealerCards = game.getDealer().getHand().getCards();
        System.out.print("Krupier ma: ");
        if (hideDealerSecondCard && dealerCards.size() >= 2) {
            System.out.println("[" + dealerCards.get(0) + ", [Ukryta Karta]]");
        } else {
            int score = game.getEvaluator().calculateValue(game.getDealer().getHand());
            System.out.println(dealerCards + " (Punkty: " + score + ")");
        }

        // Karty Gracza
        int playerScore = game.getEvaluator().calculateValue(game.getPlayer().getHand());
        System.out.println("Ty masz   : " + game.getPlayer().getHand().getCards() + " (Punkty: " + playerScore + ")");
        System.out.println("============");
    }

    private void announceWinner() {
        GameResult result = game.determineWinner();
        System.out.println("\n*** WYNIK ***");
        switch (result) {
            case PLAYER_WINS:
                System.out.println("Wygrywasz! Gratulacje!");
                break;
            case DEALER_WINS:
                System.out.println("Krupier wygrywa.");
                break;
            case PUSH:
                System.out.println("Remis (Push)!");
                break;
        }

        // --- ZAPIS DO CSV ---
        int playerScore = game.getEvaluator().calculateValue(game.getPlayer().getHand());
        int dealerScore = game.getEvaluator().calculateValue(game.getDealer().getHand());
        int playerCardCount = game.getPlayer().getHand().getCards().size();

        double profit = 0.0;
        if (result == GameResult.PLAYER_WINS) profit = 10.0;
        else if (result == GameResult.DEALER_WINS) profit = -10.0;

        GameRecord record = new GameRecord("HUMAN_PLAYER", playerScore, dealerScore, result, playerCardCount, 10.0, profit, 0.0);
        exporter.exportSingleGame(record);
        System.out.println("[Info] Wynik gry zostal zapisany do CSV.");
    }

    private boolean askPlayAgain() {
        while (true) {
            System.out.print("\nZagrać jeszcze raz? (T/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("T") || input.equals("Y")) return true;
            if (input.equals("N")) return false;
        }
    }

    public static void main(String[] args) {
        new ConsoleUI().start();
    }
}
