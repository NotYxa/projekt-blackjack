package com.blackjack.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Narzędzie do zapisu naszych obiektów GameRecord do fizycznego pliku na dysku.
 */
public class CsvExporter {
    private final String filePath;

    public CsvExporter(String filePath) {
        this.filePath = filePath; // Np. "../data-output/results.csv"
    }

    /**
     * Zapisuje jeden rekord gry jako nowy wiersz w pliku CSV.
     * Parametr argument to `GameRecord` przechowujący dane.
     */
    public void exportSingleGame(GameRecord record) {
        File file = new File(filePath);
        // Sprawdzamy czy plik wg nie istnieje lub jest całkowicie pusty
        boolean writeHeader = !file.exists() || file.length() == 0;

        // używamy flagi "true" w FileWriterze, żeby dopisywać (append) 
        // dane do pliku, a nie go nadpisywać od zera
        try (FileWriter writer = new FileWriter(filePath, true)) {
            // Jeśli to nowy/pusty plik, najpierw wstawiamy nagłówki kolumn
            if (writeHeader) {
                writer.write("StrategyName,PlayerScore,DealerScore,Result,PlayerCardCount,InitialBet,Profit,InitialTrueCount\n");
            }

            // Konwertujemy rekord do tekstu oddzielonego przecinkami
            String row = record.toCsvRow();
            
            // dopisz i wstaw nowy wiersz (\n)
            writer.write(row + "\n");
            
        } catch (IOException e) {
            System.err.println("Błąd zapisu do pliku CSV: " + e.getMessage());
        }
    }
}
