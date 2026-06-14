import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

def get_data_path():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    return os.path.normpath(os.path.join(current_dir, "..", "..", "data-output", "results.csv"))

def get_charts_dir():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    return os.path.normpath(os.path.join(current_dir, "..", "charts"))

def main():
    print("Generowanie wykresow...")
    
    csv_path = get_data_path()
    charts_dir = get_charts_dir()

    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError:
        print(f"Blad: brak pliku {csv_path}")
        return

    # Ustawiamy ladny styl dla wszystkich wykresow
    sns.set_theme(style="whitegrid")

    # --- WYKRES 1: Wykres kołowy (Pie Chart) wyników ---
    plt.figure(figsize=(8, 6))
    results_counts = df['Result'].value_counts()
    
    # Rysujemy wykres. autopct formatuje procenty na wykresie
    plt.pie(results_counts, labels=results_counts.index, autopct='%1.1f%%', 
            colors=['#ff9999','#66b3ff','#99ff99'], startangle=90)
    plt.title('Szansy na wygraną w Blackjacku (Strategia < 15 punktów)')
    
    # Zamiast wyswietlac w nowym oknie, zapisujemy jako plik PNG
    pie_path = os.path.join(charts_dir, 'win_loss_ratio.png')
    plt.savefig(pie_path)
    plt.close() # Czyscimy pamiec
    print(f"Zapisano: {pie_path}")

    # --- WYKRES 2: Histogram wyników gracza ---
    # Chcemy zobaczyc, jak czesto nasz gracz mial 21 punktow, a jak czesto przepalal (bust > 21)
    plt.figure(figsize=(10, 6))
    
    # Używamy seaborn do narysowania wierszy słupków dla kazdego wyniku
    sns.countplot(data=df, x='PlayerScore', palette='viridis')
    
    plt.title('Rozkład końcowych punktów Gracza')
    plt.xlabel('Punkty Gracza')
    plt.ylabel('Liczba gier (częstotliwość)')
    
    hist_path = os.path.join(charts_dir, 'player_score_distribution.png')
    plt.savefig(hist_path)
    plt.close()
    print(f"Zapisano: {hist_path}")

    print("Gotowe! Sprawdz folder 'charts'.")

if __name__ == "__main__":
    main()
