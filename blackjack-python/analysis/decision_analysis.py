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
    print("Analiza decyzji i wynikow...")
    
    csv_path = get_data_path()
    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError:
        print("Nie znaleziono pliku CSV.")
        return

    # Odrzucamy gry, w ktorych gracz od razu przepalil (bust > 21) 
    # bo tam nie ma co analizowac - od razu przegrywa.
    # Interesuje nas: jesli gracz zatrzyma sie na x punktow (od 15 do 21), jak czesto wygrywa?
    
    df_valid_scores = df[(df['PlayerScore'] >= 15) & (df['PlayerScore'] <= 21)]
    
    # Grupujemy po punktach gracza i liczymy ile bylo wygranych, przegranych, remisow
    summary = df_valid_scores.groupby(['PlayerScore', 'Result']).size().unstack(fill_value=0)
    
    print("\n--- ZESTAWIENIE WYNIKOW DLA POSZCZEGOLNYCH PUNKTOW GRACZA ---")
    print(summary)
    
    # Tworzymy wykres sumaryczny (Stacked Bar Chart)
    sns.set_theme(style="whitegrid")
    summary.plot(kind='bar', stacked=True, figsize=(10, 6), color=['#ff9999', '#66b3ff', '#99ff99'])
    
    plt.title('Wyniki gry w zależności od końcowych punktów gracza')
    plt.xlabel('Końcowe punkty Gracza')
    plt.ylabel('Liczba gier')
    plt.legend(title='Rezultat')
    plt.xticks(rotation=0)
    
    charts_dir = get_charts_dir()
    chart_path = os.path.join(charts_dir, 'decisions_analysis.png')
    plt.savefig(chart_path)
    plt.close()
    
    print(f"\nZapisano wykres analizy wynikow w: {chart_path}")

if __name__ == "__main__":
    main()
