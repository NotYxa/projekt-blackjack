package com.blackjack.export;

import com.blackjack.logic.GameResult;

/**
 * Klasa trzymająca "ślad" po jednej zakończonej grze.
 * Jest to tzw. obiekt DTO (Data Transfer Object) - czyste pudełko na dane.
 */
public class GameRecord {
    private final String strategyName; // Nazwa uzytej strategii (lub "HUMAN" dla gry recznej)
    private final int playerScore;
    private final int dealerScore;
    private final GameResult result;
    
    // Zapisujemy dodatkowo ile kart dobrał gracz
    private final int playerCardCount;

    // Dane finansowe i analityczne
    private final double initialBet;
    private final double profit;
    private final double initialTrueCount;

    public GameRecord(String strategyName, int playerScore, int dealerScore, GameResult result, int playerCardCount, double initialBet, double profit, double initialTrueCount) {
        this.strategyName = strategyName;
        this.playerScore = playerScore;
        this.dealerScore = dealerScore;
        this.result = result;
        this.playerCardCount = playerCardCount;
        this.initialBet = initialBet;
        this.profit = profit;
        this.initialTrueCount = initialTrueCount;
    }

    /**
     * Konwertuje ten obiekt na jeden wiersz tekstu oddzielony przecinkami (CSV).
     */
    public String toCsvRow() {
        // Zwracamy tekst w formacie: nazwa_strategii,wynik_gracza,wynik_krupiera,rezultat,liczba_kart,zaklad,zysk,true_count
        return strategyName + "," + playerScore + "," + dealerScore + "," + result.name() + "," + playerCardCount + "," + initialBet + "," + profit + "," + initialTrueCount;
    }

    // (Opcjonalnie) Możemy dodać standardowe gettery, gdyby były do czegoś potrzebne
    public int getPlayerScore() { return playerScore; }
    public int getDealerScore() { return dealerScore; }
    public GameResult getResult() { return result; }
    public int getPlayerCardCount() { return playerCardCount; }
}
