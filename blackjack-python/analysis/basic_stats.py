import pandas as pd
import os

def main():
    print("Wczytywanie wynikow gry z CSV...")
    
    # 1. Automatyczne ustalanie sciezki do pliku niezaleznie skad uruchamiamy skrypt
    current_dir = os.path.dirname(os.path.abspath(__file__))
    csv_path = os.path.join(current_dir, "..", "..", "data-output", "results.csv")
    csv_path = os.path.normpath(csv_path)

    # 2. Bezpieczne ladowanie i sprowadzenie bledow braku pliku
    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError:
        print(f"Blad: Nie znaleziono pliku pod sciezka: {csv_path}")
        print("Uruchom najpierw symulacje w Javie (SimulationRunner.java)!")
        return

    print(f"Udalo sie! Przeanalizowano {len(df)} partii z krupierem.\n")

    # 3. Wyciagamy procentowe statystyki (win rate, push rate, lose rate)
    print("--- PODSUMOWANIE PROCENTOWE ---")
    results_counts = df['Result'].value_counts()
    results_percent = df['Result'].value_counts(normalize=True) * 100

    for result, count in results_counts.items():
        percent = results_percent[result]
        print(f"{result}: {count} sztuk ({percent:.2f}%)")

    # 4. Sprawdzamy srednia ilosc kart na stole
    avg_cards = df['PlayerCardCount'].mean()
    print(f"\nŚrednio jako gracz dobieralismy {avg_cards:.2f} kart na partie.")

if __name__ == "__main__":
    main()
