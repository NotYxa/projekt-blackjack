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
    print("Rozpoczecie analizy miedzystrategicznej...")
    
    csv_path = get_data_path()
    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError:
        print("Nie znaleziono pliku CSV. Uruchom najpierw SimulationRunner.java!")
        return

    # Usuniecie manualnych gier do statystyk (jesli jakies zagralismy ConsoleUI.java)
    df = df[df['StrategyName'] != 'HUMAN_PLAYER']

    # Obliczamy Win Rate: (wygrane / wszystkie_gry) * 100
    # Obliczamy tez Bust Rate: jak czesto gracz ladowal z > 21 punktami
    strategy_groups = df.groupby('StrategyName')
    
    stats = []
    
    for name, group in strategy_groups:
        total = len(group)
        wins = len(group[group['Result'] == 'PLAYER_WINS'])
        pushes = len(group[group['Result'] == 'PUSH'])
        busts = len(group[group['PlayerScore'] > 21])
        
        # Jeśli nasz plik ma nową kolumnę Profit, podliczmy ją!
        total_profit = 0
        if 'Profit' in group.columns:
            total_profit = group['Profit'].sum()

        win_rate = (wins / total) * 100
        push_rate = (pushes / total) * 100
        bust_rate = (busts / total) * 100
        # W kasynach "bezpieczny win-rate" to zazwyczaj Win+Push
        non_loss_rate = win_rate + push_rate 
        
        stats.append({
            'Strategia': name,
            'Win Rate (%)': win_rate,
            'Push Rate (%)': push_rate,
            'Brak Przegranej (%)': non_loss_rate,
            'Bust Rate (%)': bust_rate,
            'Finansowy Zysk ($)': total_profit
        })
        
    stats_df = pd.DataFrame(stats).sort_values(by='Finansowy Zysk ($)', ascending=False)
    
    print("\n--- WYNIKI TURNIEJU STRATEGII (ZWYCIEZCA FINANSOWY NA GORZE) ---")
    print(stats_df.to_string(index=False, float_format="%.2f"))
    
    # Rysujemy wygenerowane wyniki!
    sns.set_theme(style="whitegrid")
    
    # UWAGA: Tworzymy dwa wykresy!
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 6))
    
    # 1. Wykres wskaźnika zabezpieczeń (Win + Push)
    sns.barplot(data=stats_df, x='Strategia', y='Brak Przegranej (%)', hue='Strategia', legend=False, palette='viridis', ax=ax1)
    ax1.set_title('Skuteczność strategii (Win + Push)')
    ax1.set_ylim(0, 100)
    for p in ax1.patches:
        ax1.annotate(format(p.get_height(), '.1f') + '%', 
                   (p.get_x() + p.get_width() / 2., p.get_height()), 
                   ha = 'center', va = 'center', 
                   xytext = (0, -15), textcoords = 'offset points',
                   color='white', weight='bold')

    # 2. Wykres czystego ZYSKU FINANSOWEGO (Z uwzględnieniem liczenia kart)
    sns.barplot(data=stats_df, x='Strategia', y='Finansowy Zysk ($)', hue='Strategia', legend=False, palette='coolwarm', ax=ax2)
    ax2.set_title('Zakończenie finansowe (Profit) po X grach')
    for p in ax2.patches:
        ax2.annotate(format(p.get_height(), '.0f') + '$', 
                   (p.get_x() + p.get_width() / 2., p.get_height() if p.get_height() > 0 else 0), 
                   ha = 'center', va = 'bottom' if p.get_height() > 0 else 'top', 
                   xytext = (0, 5 if p.get_height() > 0 else -15), textcoords = 'offset points',
                   color='black', weight='bold')

    plt.tight_layout()
    charts_dir = get_charts_dir()
    chart_path = os.path.join(charts_dir, 'strategy_comparison_profit.png')
    plt.savefig(chart_path)
    plt.close()
    
    print(f"\nWygenerowano podwójny wykres do: {chart_path}")

if __name__ == "__main__":
    main()
