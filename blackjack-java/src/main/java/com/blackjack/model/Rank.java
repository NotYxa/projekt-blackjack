package com.blackjack.model;

/**
 * Wartość karty (figura / liczba).
 *
 * Każda stała enum ma przypisaną:
 * - displayName: jak wypisać kartę na ekranie (np. "A", "K", "7")
 * - value:       wartość punktowa w Blackjacku
 *
 * As (ACE) ma tutaj wartość 11.
 * Obsługa wariantu "As = 1" będzie w klasie HandEvaluator — nie tutaj.
 * Dzięki temu każda klasa robi tylko jedną rzecz.
 */
public enum Rank {
    TWO   ("2",  2),
    THREE ("3",  3),
    FOUR  ("4",  4),
    FIVE  ("5",  5),
    SIX   ("6",  6),
    SEVEN ("7",  7),
    EIGHT ("8",  8),
    NINE  ("9",  9),
    TEN   ("10", 10),
    JACK  ("J",  10),  // Walet
    QUEEN ("Q",  10),  // Dama
    KING  ("K",  10),  // Król
    ACE   ("A",  11);  // As — domyślnie 11, HandEvaluator zmieni na 1 gdy trzeba

    // Pola przechowujące wartości dla każdej stałej
    private final String displayName;
    private final int value;

    // Konstruktor enuma — wywołuje się raz dla każdej stałej powyżej
    Rank(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }
}
