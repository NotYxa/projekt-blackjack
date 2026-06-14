package com.blackjack.simulation;

import com.blackjack.logic.BlackjackGame;
import com.blackjack.logic.HandEvaluator;
import com.blackjack.model.Card;
import com.blackjack.model.Hand;

/**
 * Zbiór różnych strategii dla naszego Bota.
 */
public class BasicStrategy {
    public enum StrategyType {
        SIMPLE_15,
        MIMIC_DEALER,
        NEVER_BUST,
        ADVANCED_CASINO,
        COUNTER_HI_LO,
        COUNTER_ZEN
    }

    private final HandEvaluator evaluator;
    private final StrategyType currentStrategy;
    private BlackjackGame game; // Używamy, by podejrzeć liczenie kart z buta!

    public BasicStrategy(HandEvaluator evaluator, StrategyType currentStrategy) {
        this.evaluator = evaluator;
        this.currentStrategy = currentStrategy;
    }

    public void setGame(BlackjackGame game) {
        this.game = game;
    }


    public StrategyType getCurrentStrategy() {
        return currentStrategy;
    }

    public boolean shouldSplit(Hand playerHand, Card dealerUpcard) {
        // Tylko przy dwóch kartach tej samej wartości.
        if (playerHand.getCards().size() != 2) return false;
        Card c1 = playerHand.getCards().get(0);
        Card c2 = playerHand.getCards().get(1);
        if (c1.getRank().getValue() != c2.getRank().getValue()) return false;

        switch (currentStrategy) {
            case ADVANCED_CASINO: return splitAdvanced(c1, dealerUpcard);
            case COUNTER_HI_LO:   return splitCardCounterHiLo(c1, dealerUpcard);
            case COUNTER_ZEN:     return splitCardCounterZen(c1, dealerUpcard);
            default:              return false; // Proste strategie nie dublują i nie splitują
        }
    }

    /**
     * Główna metoda wywoływana przez symulator. Powinna decydować, jak zagrać w danej turze.
     */
    public boolean shouldHit(Hand playerHand, Card dealerUpcard) {
        switch (currentStrategy) {
            case SIMPLE_15:       return strategySimple(playerHand);
            case MIMIC_DEALER:    return strategyMimicDealer(playerHand);
            case NEVER_BUST:      return strategyNeverBust(playerHand);
            case ADVANCED_CASINO: return strategyAdvanced(playerHand, dealerUpcard);
            case COUNTER_HI_LO:   return strategyCardCounterHiLo(playerHand, dealerUpcard);
            case COUNTER_ZEN:     return strategyCardCounterZen(playerHand, dealerUpcard);
            default:              return strategySimple(playerHand);
        }
    }

    /**
     * Decyzja o podwojeniu stawki (Double Down).
     * Wykonywana tylko przy pierwszych 2 kartach.
     */
    public boolean shouldDoubleDown(Hand playerHand, Card dealerUpcard) {
        switch (currentStrategy) {
            case ADVANCED_CASINO: return doubleDownAdvanced(playerHand, dealerUpcard);
            case COUNTER_HI_LO:   return doubleDownCardCounterHiLo(playerHand, dealerUpcard);
            case COUNTER_ZEN:     return doubleDownCardCounterZen(playerHand, dealerUpcard);
            default:              return false; // Proste strategie (Simple, Mimic, NeverBust) nie dublują
        }
    }

    // ==========================================================
    //                    LISTA TWOICH STRATEGII
    // ==========================================================

    /**
     * WERSJA 1: Nasza stara, testowana - po prostu ciągnie do 15 i staje (niezależnie co ma krupier).
     */
    private boolean strategySimple(Hand playerHand) {
        return evaluator.calculateValue(playerHand) < 15;
    }

    /**
     * WERSJA 2: Naśladowca. Gracz ufa krupierom z kasyna i tak jak oni dobiera zawsze do 17 punktów, a potem pasuje.
     */
    private boolean strategyMimicDealer(Hand playerHand) {
        return evaluator.calculateValue(playerHand) < 17;
    }

    /**
     * WERSJA 3: Strachliwa gra (Never Bust).
     * Bierzemy kartę tylko wtedy, kiedy NIE MA ŻADNEGO RYZYKA przekroczenia 21 punktów.
     * Ponieważ maksymalna wartość karty, jaką można dostać z talii to 10... zatrzymujemy się już na 12 pkt.
     */
    private boolean strategyNeverBust(Hand playerHand) {
        return evaluator.calculateValue(playerHand) < 12;
    }

    private boolean isSoft(Hand hand) {
        int total = 0;
        int acesCount = 0;
        for (Card card : hand.getCards()) {
            total += card.getValue();
            if (card.getRank() == com.blackjack.model.Rank.ACE) {
                acesCount++;
            }
        }
        while (total > 21 && acesCount > 0) {
            total -= 10;
            acesCount--;
        }
        return acesCount > 0;
    }

    /**
     * WERSJA 4: Prawdziwa szkolna strategia (Kasynowa "Basic Strategy").
     * Podejmując decyzje zwracamy w końcu uwagę, co wylosował rywal naprzeciw nas!
     */
    private boolean strategyAdvanced(Hand playerHand, Card dealerUpcard) {
        int score = evaluator.calculateValue(playerHand);
        int dealerVal = dealerUpcard.getValue();
        boolean soft = isSoft(playerHand);

        if (soft) {
            // Soft Totals:
            if (score <= 17) return true; // Zawsze HIT Soft 17 lub mniej (o ile nie zdublujemy, double jest obsluzony oddzielnie)
            if (score == 18) {
                // Soft 18: STAND przeciwko 2-8, HIT przeciwko 9, 10, A
                if (dealerVal >= 9) return true; // (Dla dealerVal 11 jako A)
                return false; 
            }
            return false; // Soft 19+ -> STAND
        } else {
            // Hard Totals:
            // 11 lub mniej: Zawsze ciągnij (jeśli nie dublujemy)
            if (score <= 11) return true;
            
            // 17 lub więcej: Zawsze STAND
            if (score >= 17) return false;

            // 12: Wpadka tylko jak krupier ma 4, 5, 6
            if (score == 12) {
                if (dealerVal >= 4 && dealerVal <= 6) return false;
                return true;
            }

            // 13 - 16: STAND jeśli krupier ma 2 - 6 (czyli ma szansę na bust)
            if (dealerVal >= 2 && dealerVal <= 6) {
                return false; 
            }

            return true; // HIT jeśli the krupier ma wysoką (7-A)
        }
    }

    /**
     * WERSJA 5: Liczenie Kart (Hi-Lo System).
     */
    private boolean strategyCardCounterHiLo(Hand playerHand, Card dealerUpcard) {
        if (game == null) {
            return strategyAdvanced(playerHand, dealerUpcard);
        }

        double trueCount = game.getDeck().getTrueCount();
        int score = evaluator.calculateValue(playerHand);
        int dealerVal = dealerUpcard.getValue();

        // Jeśli krupier ma 10, a w talii zostało bardzo dużo 10-tek (True Count > 0), i my mamy 16:
        if (score == 16 && dealerVal >= 10 && trueCount > 0) {
            return false;
        }
        if (score == 15 && dealerVal >= 10 && trueCount > 3) {
            return false;
        }

        return strategyAdvanced(playerHand, dealerUpcard);
    }

    /**
     * WERSJA 6: Liczenie Kart (Zaawansowany Zen Count).
     */
    private boolean strategyCardCounterZen(Hand playerHand, Card dealerUpcard) {
        if (game == null) {
            return strategyAdvanced(playerHand, dealerUpcard);
        }

        double zenTrueCount = game.getDeck().getZenTrueCount();
        int score = evaluator.calculateValue(playerHand);
        int dealerVal = dealerUpcard.getValue();

        // Zen Count jest potężniejszy i precyzyjniejszy 
        if (score == 16 && dealerVal >= 10 && zenTrueCount > 0) return false;
        if (score == 15 && dealerVal >= 10 && zenTrueCount > 4) return false;
        // Wiele innych indeksów Zen Count można dodać, ale ten core wystarczy 

        return strategyAdvanced(playerHand, dealerUpcard);
    }
    
    // ==========================================================
    //                      DOUBLE DOWN LOGIC
    // ==========================================================

    private boolean doubleDownAdvanced(Hand playerHand, Card dealerUpcard) {
        int score = evaluator.calculateValue(playerHand);
        int dealerVal = dealerUpcard.getRank().getValue();
        boolean soft = isSoft(playerHand);

        if (soft) {
            // Soft Totals Double
            if (score == 13 || score == 14) {
                if (dealerVal == 5 || dealerVal == 6) return true;
            } else if (score == 15 || score == 16) {
                if (dealerVal >= 4 && dealerVal <= 6) return true;
            } else if (score == 17 || score == 18) {
                if (dealerVal >= 3 && dealerVal <= 6) return true;
            }
        } else {
            // Hard Totals Double
            if (score == 11) return true; 
            if (score == 10 && dealerVal >= 2 && dealerVal <= 9) return true;
            if (score == 9 && dealerVal >= 3 && dealerVal <= 6) return true;
        }

        return false;
    }

    private boolean doubleDownCardCounterHiLo(Hand playerHand, Card dealerUpcard) {
        if (game == null) return doubleDownAdvanced(playerHand, dealerUpcard);
        
        double trueCount = game.getDeck().getTrueCount();
        int score = evaluator.calculateValue(playerHand);
        int dealerVal = dealerUpcard.getRank().getValue();

        // Standardowe zagrania kasynowe
        if (doubleDownAdvanced(playerHand, dealerUpcard)) return true;

        if (score == 10 && dealerVal == 10 && trueCount >= 4) return true;
        if (score == 9 && dealerVal == 2 && trueCount >= 1) return true;
        if (score == 9 && dealerVal == 7 && trueCount >= 3) return true;
        if (score == 8 && dealerVal == 6 && trueCount >= 2) return true;

        return false;
    }

    private boolean doubleDownCardCounterZen(Hand playerHand, Card dealerUpcard) {
        if (game == null) return doubleDownAdvanced(playerHand, dealerUpcard);
        
        double zenTrueCount = game.getDeck().getZenTrueCount();
        int score = evaluator.calculateValue(playerHand);
        int dealerVal = dealerUpcard.getRank().getValue();

        if (doubleDownAdvanced(playerHand, dealerUpcard)) return true;

        // Indeksy mogą być zbliżone do HiLo ale na generalnie nieco wyższych liczbach naturalnych
        if (score == 10 && dealerVal == 10 && zenTrueCount >= 6) return true;
        if (score == 9 && dealerVal == 2 && zenTrueCount >= 2) return true;
        if (score == 9 && dealerVal == 7 && zenTrueCount >= 5) return true;
        if (score == 8 && dealerVal == 6 && zenTrueCount >= 3) return true;

        return false;
    }

    // ==========================================================
    //                      SPLIT LOGIC
    // ==========================================================

    private boolean splitAdvanced(Card playerCard, Card dealerUpcard) {
        int cardVal = playerCard.getRank().getValue();
        int dealerVal = dealerUpcard.getRank().getValue();

        // 8 i Asy z automatu
        if (cardVal == 11 || cardVal == 8) return true;
        
        // 10, 5 - nigdy
        if (cardVal == 10 || cardVal == 5) return false;

        // 9 - split przeciwko 2-9 oprócz 7
        if (cardVal == 9) {
            if (dealerVal >= 2 && dealerVal <= 9 && dealerVal != 7) return true;
            return false;
        }

        // 7 - split przeciwko 2-7
        if (cardVal == 7 && dealerVal >= 2 && dealerVal <= 7) return true;

        // 6 - split przeciwko 2-6
        if (cardVal == 6 && dealerVal >= 2 && dealerVal <= 6) return true;

        // 4 - split przeciw 5-6
        if (cardVal == 4 && (dealerVal == 5 || dealerVal == 6)) return true;

        // 2, 3 - split przeciw 2-7
        if ((cardVal == 2 || cardVal == 3) && dealerVal >= 2 && dealerVal <= 7) return true;

        return false;
    }

    private boolean splitCardCounterHiLo(Card playerCard, Card dealerUpcard) {
        if (game == null) return splitAdvanced(playerCard, dealerUpcard);

        double trueCount = game.getDeck().getTrueCount();
        int cardVal = playerCard.getRank().getValue();
        int dealerVal = dealerUpcard.getRank().getValue();

        if (splitAdvanced(playerCard, dealerUpcard)) return true;

        if (cardVal == 10 && dealerVal == 6 && trueCount >= 4) return true;
        if (cardVal == 10 && dealerVal == 5 && trueCount >= 5) return true;

        return false;
    }

    private boolean splitCardCounterZen(Card playerCard, Card dealerUpcard) {
        if (game == null) return splitAdvanced(playerCard, dealerUpcard);

        double zenTrueCount = game.getDeck().getZenTrueCount();
        int cardVal = playerCard.getRank().getValue();
        int dealerVal = dealerUpcard.getRank().getValue();

        if (splitAdvanced(playerCard, dealerUpcard)) return true;

        if (cardVal == 10 && dealerVal == 6 && zenTrueCount >= 6) return true;
        
        return false;
    }
}
