package com.blackjack.simulation;

import com.blackjack.export.CsvExporter;
import com.blackjack.export.GameRecord;
import com.blackjack.logic.BlackjackGame;
import com.blackjack.logic.GameResult;
import com.blackjack.logic.HandEvaluator;
import com.blackjack.model.Card;
import com.blackjack.model.Hand;
/**
 * Uruchamia symulację wielu rozdań (np. 10 000 razy w ułamek sekundy).

 * Jest przeciwieństwem wirtualnej konsoli (nie czeka na użytkownika).
 */
public class SimulationRunner {

    private final int numberOfGames;
    private final CsvExporter exporter;
    private final BlackjackGame game; // Przechowujemy jedną grę, aby zachować stan 'buta' z kartami

    public SimulationRunner(int numberOfGames, String exportPath) {
        this.numberOfGames = numberOfGames;
        
        // Przed uruchomieniem kasujemy stary plik z wynikami, żeby zacząć na czysto!
        java.io.File oldFile = new java.io.File(exportPath);
        if(oldFile.exists()) oldFile.delete();
        
        this.exporter = new CsvExporter(exportPath);
        this.game = new BlackjackGame(); 
    }

    public void runSimulation() {
        System.out.println("Rozpoczynam TURNIEJ STRATEGII (" + numberOfGames + " gier na każdą)...");
        long startTime = System.currentTimeMillis();

        // Pętla odpala po kolei wszystkie dostępne strategie zdefiniowane w Enumie!
        for (BasicStrategy.StrategyType type : BasicStrategy.StrategyType.values()) {
            System.out.println("-> Symuluję strategię: " + type.name());
            // Zresetuj but (talię) przy nowej strategii, aby wszyscy mieli rówe szanse
            game.getDeck().reset();
            game.getDeck().shuffle();
            
            BasicStrategy strategy = new BasicStrategy(new HandEvaluator(), type);
            strategy.setGame(game);
            
            for (int i = 0; i < numberOfGames; i++) {
                playSingleGame(strategy);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Koniec symulacji! Wykonano całą planszę w " + (endTime - startTime) + " ms.");
    }

    private void playSingleGame(BasicStrategy strategy) {
        // == SPRAWDŹ TRUE COUNT == ZANIM ROZPOCZNIESZ RUNDĘ ==
        double initialTrueCount = game.getDeck().getTrueCount();

        // == LOGIKA STAWKOWANIA (MONEY MANAGEMENT) ==
        // Domyślny zakład w kasynie
        double currentBet = 10.0;
        
        // Zaskakująca cecha Liczącego Karty - wykorzystuje wiedzę z licznika i stawia ostro w dobrych monetach!
        if (strategy.getCurrentStrategy() == BasicStrategy.StrategyType.COUNTER_HI_LO) {
            double initialTrueCountHiLo = game.getDeck().getTrueCount();
            if (initialTrueCountHiLo >= 5) {
                currentBet = 500.0; // ALL IN! Potężna przewaga
            } else if (initialTrueCountHiLo >= 3) {
                currentBet = 150.0; // Zwiększona pewność
            } else if (initialTrueCountHiLo >= 1) {
                currentBet = 40.0; // Optymistycznie
            } else {
                currentBet = 1.0; // Poniżej True Count +1 po prostu "siedzimy przy stole i czekamy" (minimalny bet)
            }
        } else if (strategy.getCurrentStrategy() == BasicStrategy.StrategyType.COUNTER_ZEN) {
            double initialTrueCountZen = game.getDeck().getZenTrueCount();
            // Zen ma nieco inne współczynniki i wyżej oscyluje, dostosowujemy progi:
            if (initialTrueCountZen >= 8) {
                currentBet = 500.0;
            } else if (initialTrueCountZen >= 5) {
                currentBet = 150.0;
            } else if (initialTrueCountZen >= 2) {
                currentBet = 40.0;
            } else {
                currentBet = 1.0;
            }
        }

        game.startRound();

        // Wyciągamy karty gracza
        Hand playerHand = game.getPlayer().getHand();
        Card dealerUpcard = game.getDealer().getHand().getCards().get(0);

        // 1. Sprawdzenie Splita
        boolean hasSplit = false;
        Hand splitHand1 = null;
        Hand splitHand2 = null;
        double splitBet = currentBet; // Stawka na 2 rękę to tyle samo co na 1

        if (strategy.shouldSplit(playerHand, dealerUpcard)) {
            hasSplit = true;
            splitHand1 = new Hand();
            splitHand2 = new Hand();
            
            // Rozdanie kart
            splitHand1.addCard(playerHand.getCards().get(0));
            splitHand1.addCard(game.getDeck().dealCard());
            
            splitHand2.addCard(playerHand.getCards().get(1));
            splitHand2.addCard(game.getDeck().dealCard());
        }

        // ============================
        // LOGIKA ROZGRYWANIA RĄK
        // ============================
        
        java.util.List<Hand> handsToPlay = new java.util.ArrayList<>();
        java.util.List<Double> betsToPlay = new java.util.ArrayList<>();
        
        if (hasSplit) {
            handsToPlay.add(splitHand1);
            handsToPlay.add(splitHand2);
            betsToPlay.add(currentBet);
            betsToPlay.add(splitBet);
        } else {
            handsToPlay.add(playerHand);
            betsToPlay.add(currentBet);
        }

        boolean anyoneNotBusted = false;

        for (int h = 0; h < handsToPlay.size(); h++) {
            Hand currentHandContext = handsToPlay.get(h);
            game.getPlayer().setHand(currentHandContext); // Podłączamy rękę do gry
            
            boolean hasDoubled = false;
            // Sprawdzamy Double Down dla tej ręki
            if (currentHandContext.getCards().size() == 2 && strategy.shouldDoubleDown(currentHandContext, dealerUpcard)) {
                betsToPlay.set(h, betsToPlay.get(h) * 2.0);
                game.playerHit();
                hasDoubled = true;
            }

            if (!hasDoubled) {
                while (!game.isPlayerBusted() && strategy.shouldHit(currentHandContext, dealerUpcard)) {
                    game.playerHit();
                }
            }
            
            if (!game.isPlayerBusted()) {
                anyoneNotBusted = true;
            }
        }

        // 3. Tura krupiera uruchamia się raz, jeśli jakakolwiek ręka przetrwała
        // Zabezpieczenie: Dealer turn w BlackjackGame używa hand krupiera.
        if (anyoneNotBusted) {
            game.playDealerTurn();
        }

        int dealerScore = game.getEvaluator().calculateValue(game.getDealer().getHand());

        // 4. Podsumowanie wyników dla wszystkich rąk i zapis
        for (int h = 0; h < handsToPlay.size(); h++) {
            Hand finishedHand = handsToPlay.get(h);
            double bet = betsToPlay.get(h);
            game.getPlayer().setHand(finishedHand); // Do determinewinner()
            
            GameResult result = game.determineWinner();
            int playerScore = game.getEvaluator().calculateValue(finishedHand);
            int playerCardCount = finishedHand.getCards().size();

            // == OBLICZAMY ZYSKI/STRATY (Profit) ==
            double profit = 0.0;
            if (result == GameResult.PLAYER_WINS) {
                if (playerCardCount == 2 && playerScore == 21 && !hasSplit) {
                    profit = bet * 1.5; 
                } else {
                    profit = bet; 
                }
            } else if (result == GameResult.DEALER_WINS) {
                profit = -bet;
            }

            // 5. Zapis do CSV (Każda zagrana ręka po splicie to jeden wpis w CSV)
            GameRecord record = new GameRecord(
                    strategy.getCurrentStrategy().name(), 
                    playerScore, dealerScore, result, playerCardCount,
                    bet, profit, initialTrueCount
            );
            exporter.exportSingleGame(record);
        }
    }

    // Aby uruchomić tylko symulację po prostu wywołujemy SimulationRunner.main
    public static void main(String[] args) {
        // Tłuczemy 100 000 partii dla lepszej statystyki (prawo wielkich liczb!), a logi z tego wrzucamy do naszego results.csv
        SimulationRunner simulator = new SimulationRunner(100000, "C:\\Users\\Dell\\Documents\\GitHub\\projekt-blackjack\\data-output\\results.csv");
        simulator.runSimulation();
    }
}
