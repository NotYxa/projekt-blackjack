# Projekt Blackjack: Symulator i Analiza Statystyczna

Zaawansowany symulator gry Blackjack zaimplementowany w języku **Java**, wyposażony w moduł uczenia maszynowego (automatycznych decyzji) oraz mechanizmy analizy danych przy użyciu języka **Python** (Pandas, Seaborn).

Projekt pozwala z niesamowitą szybkością rozegrać miliony rozdań pod nadzorem różnych algorytmów analitycznych (BOTów). Udowadnia matematycznie przewagę kasyna w długim terminie i ukazuje, w jakim stopniu **liczenie kart (Hi-Lo, Zen Count)** potrafi to ryzyko zminimalizować, a nawet odwrócić expected value (EV) na korzyść gracza.

## Architektura i Koncepcja
Aplikacja została ściśle podzielona na dwa mikroserwisy (warstwy), aby odseparować silnik generujący zdarzenia od warstwy podsumowującej liczby.

1. **Silnik Symulacyjny (Java 17 / Maven)** - Orkiestruje zasady gry, przydziela karty z buta (Shoe) i oblicza matematycznie optymalne ruchy bota przy stole za pomocą klasy `BasicStrategy`. Odpowiada za logikę liczenia kart i Bet Spreading ($1 - $500). Rezultatami setek tysięcy gier pluje bez obciążenia pamięci prosto do wierszowego pliku CSV.
2. **Platforma Analityczna (Python 3)** - System zbudowano w celu wizualizacji surowych danych. Służą do tego biblioteki `Pandas` (data mining) i `Seaborn` (wykresy wizualne). Pokazuje ostateczne różnice miedzy algorytmami na pięknych wykresach typu bar/plot.

---

## 🤖 Wdrożone Algorytmy i Strategie

Silnik posiada 6 różnych modułów decyzyjnych gracza, począwszy od algorytmów uproszczonych, aż po algorytmy oszukujące prawdopodobieństwo:

| Nazwa strategii           | Algorytm i Działanie |
|---------------------------|----------------------------------------------------------|
| **Simple 15**             | Dobiera karty póki nie osiągnie minimum 15 pkt. |
| **Mimic Dealer**          | Małpuje zachowanie krupiera; twarde żelazne stanięcie na wartości 17. |
| **Never Bust**            | System tchórzliwy - nie dobiera gdy ma ponad 11, bo boi się spalić. |
| **Advanced Casino**       | Książkowa, idealna strategia matematyczna uwzględniająca Double Down i tabelę kasynową. Gra płaskimi stawkami (Flat betting). |
| **Counter Hi-Lo**         | Śledzi układ odnalezionych kart systemem poziomu 1 (+1 / -1). Jeśli *True Count* staje się duży, wykorzystuje wahania statystyki, aby potężnie podwyższyć zakład i zgarnąć dużą pulę. Zmienia ułamkowo ruchy (tzw. "Deviations").|
| **Counter Zen (Poziom 2)**| Agresywny algorytm liczący typu 2 (Level-2 Counting). Dokładniej określa wagę kart niskich i średnich (aż +2). Nadrabia mniejszą częstotliwość wystąpień dokładniejszymi zyskami. Posiada zmodyfikowaną tabelę Bet-Spread. |

---

## 🛠 Instalacja i Uruchomienie

### 1. Wymagania:
* Java (wersja 17 lub wyższa) + Maven
* Python (wersja 3.8 lub wyższa) z zainstalowanym `pandas`, `matplotlib`, `seaborn`

### 2. Rozruch Silnika Gry (Java)
Uruchomienie generatora (symulacji). Wygeneruje ona potężny plik tekstowy (.csv).
```bash
cd blackjack-java
mvn clean install
mvn exec:java -Dexec.mainClass="com.blackjack.ui.ConsoleUI"
```

### 3. Rendering Danych Cząstkowych (Python)
Następnie wystarczy przejść do folderu ze skryptem analityki. System natychmiast skonsumuje CSV i zapisze raporty (.png).
```bash
cd ../blackjack-python/analysis
pip install -r requirements.txt  # jezeli jeszcze nie masz
python compare_strategies.py
```

## Moduł Krupiera (Deck i But)
Zastosowaliśmy profesjonalną penetrację kasynową - The Cut Card zablokowana jest na głębokości ok ~75% kart. But zawsze składa się z 6 Standardowych Talii po 52 karty.

## Najciężej zdobyte wnioski
1. Samo liczenie kart, bez absolutnie żelaznego **Książkowego Basic Strategy** przynosi zerowy sens. Gra agresywna polega na budowaniu ułamków procenta przewagi. Nierozdzielenie pary asów obala wielogodzinną pracę systemu zliczającego talie.
2. Card Counting naturalnie **nie wygrywa większej liczby rozdań**. Generuje nadwyżkę w całkowitym kapitale gracza, grając minimalnymi zakładami (lub całkowicie porzucając grę), gdy na horyzoncie jest zły moment, a obładowując ryzykowną pozycję na full margin, gdy statystyka w talii (True Count) świeci się na czerwono.
