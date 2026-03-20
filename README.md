# Projekt Blackjack — Java + Python

Konsolowa gra Blackjack z modułem symulacji i analizy statystycznej.

## Struktura projektu

```
projekt-blackjack/
│
├── blackjack-java/                        # Cała logika gry w Javie
│   └── src/
│       ├── main/java/com/blackjack/
│       │   ├── model/                     # Obiekty: karta, talia, ręka, gracz
│       │   ├── logic/                     # Zasady gry, liczenie punktów
│       │   ├── simulation/                # Symulacja wielu gier
│       │   ├── export/                    # Zapis wyników do CSV
│       │   └── ui/                        # Interfejs konsolowy (wejście/wyjście)
│       └── test/java/com/blackjack/       # Testy jednostkowe
│
├── blackjack-python/                      # Analiza danych w Pythonie
│   ├── data/                              # Lokalna kopia CSV (opcjonalnie)
│   ├── analysis/                          # Skrypty analizujące dane
│   └── charts/                            # Wygenerowane wykresy
│
├── data-output/                           # CSV generowane przez Javę
└── docs/                                  # Notatki, diagramy, przemyślenia
```

---

## Pakiety Java i ich odpowiedzialność

### `com.blackjack.model` — obiekty danych
Tutaj mieszkają "rzeczy" w grze. Nie zawierają logiki gry — tylko dane.

| Klasa | Co robi |
|---|---|
| `Suit` | Enum: HEARTS, DIAMONDS, CLUBS, SPADES — kolory kart |
| `Rank` | Enum: TWO–TEN, JACK, QUEEN, KING, ACE — wartości kart |
| `Card` | Jedna karta (Rank + Suit). Umie się wypisać, np. "ACE of SPADES" |
| `Deck` | Talia 52 kart. Umie się potasować i wydać kartę |
| `Hand` | Ręka gracza lub krupiera — lista kart + liczenie punktów + obsługa Asa |

### `com.blackjack.logic` — zasady gry
Tutaj mieszkają "reguły". Klasy te operują na obiektach z `model`.

| Klasa | Co robi |
|---|---|
| `HandEvaluator` | Liczy punkty ręki (obsługuje Asa jako 1 lub 11) |
| `DealerStrategy` | Logika krupiera: dobiera karty poniżej 17, staje na 17+ |
| `GameResult` | Enum lub klasa: WIN, LOSE, PUSH (remis), BLACKJACK |
| `GameRules` | Stałe i reguły: krupier staje na 17, Blackjack = 21 z 2 kart, itp. |

### `com.blackjack.ui` — interfejs konsolowy
Odpowiada wyłącznie za komunikację z użytkownikiem.

| Klasa | Co robi |
|---|---|
| `ConsoleInput` | Czyta decyzje gracza (hit/stand) z klawiatury |
| `ConsoleOutput` | Wypisuje stan gry, karty, wyniki |

### `com.blackjack.export` — zapis danych
Zbiera dane z rozegranych gier i zapisuje je do CSV.

| Klasa | Co robi |
|---|---|
| `GameRecord` | Jeden rekord gry: karty, wynik, decyzje gracza |
| `CsvExporter` | Zapisuje listę `GameRecord` do pliku CSV |

### `com.blackjack.simulation` — symulacja (etap 2)
Uruchamia wiele gier automatycznie bez udziału człowieka.

| Klasa | Co robi |
|---|---|
| `SimulationRunner` | Rozgrywa N gier automatycznie |
| `BasicStrategy` | Prosta strategia decyzji gracza (na podstawie tabeli Blackjack) |

---

## Kolejność implementacji

### ETAP 1 — Fundament modelu (zacznij tutaj)
Nie ma żadnej logiki gry — tylko czyste obiekty danych.

1. `Rank` (enum z wartościami)
2. `Suit` (enum z kolorami)
3. `Card` (trzyma Rank + Suit, umie się wypisać)
4. `Deck` (lista 52 kart, tasowanie, dobieranie)
5. `Hand` (lista kart, prosta suma punktów bez Asa na start)

**Cel etapu 1:** Móc napisać kod: "stwórz talię, dobierz kartę, dodaj do ręki".

---

### ETAP 2 — Liczenie punktów
6. `HandEvaluator` — logika sumowania z obsługą Asa (1 lub 11)
7. `GameRules` — stałe: limit krupiera (17), wartość Blackjacka (21)

**Cel etapu 2:** Móc sprawdzić: "czy gracz ma bust? Czy ma Blackjacka?"

---

### ETAP 3 — Prosta gra konsolowa
8. `ConsoleOutput` — wypisywanie kart i punktów
9. `ConsoleInput` — odczyt decyzji gracza
10. `DealerStrategy` — logika ruchów krupiera
11. `GameResult` — wyznaczanie zwycięzcy
12. Główna klasa `Main` — spina wszystko w jedną grę

**Cel etapu 3:** Zagrać pełną partię w konsoli: rozdanie, hit/stand, wynik.

---

### ETAP 4 — Zapis do CSV
13. `GameRecord` — struktura danych jednej rozegrianej gry
14. `CsvExporter` — zapis do pliku w `data-output/`

**Cel etapu 4:** Po każdej grze powstaje wpis w CSV.

---

### ETAP 5 — Symulacja (wiele gier)
15. `BasicStrategy` — automatyczna strategia gracza
16. `SimulationRunner` — pętla N gier bez konsoli

**Cel etapu 5:** Uruchomić 10 000 gier i zebrać dane.

---

### ETAP 6 — Analiza w Pythonie
17. Wczytanie CSV (pandas)
18. Statystyki: win rate, push rate, bust rate
19. Wykresy (matplotlib / seaborn)
20. Analiza decyzji gracza

---

## Ważne zasady projektu
- Prostota ponad spryt — kod ma być czytelny
- Każda klasa robi jedną rzecz (zasada SRP)
- Etap po etapie — nie skacz do przodu
- Konsola na początku, żadnego GUI
- Brak bazy danych — tylko CSV
