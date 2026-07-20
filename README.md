# MoneyWise

Slimme salariscalculator - zie wat je geld echt kost in werktijd.

## Functies

### Salaris
- **Basis gegevens** - Uurloon, uren per week, leeftijd
- **Hoofd toeslagen** - Vakantiegeld, ATV, Verlof (altijd zichtbaar, met schakelaars)
- **Overige toeslagen** - Eindejaarsbonus, overwerk, weekend, nacht, ploegen (inklapbaar)
- **Aftrekposten** - 3 velden voor aftrek in % (bijv. pensioen, zorgverzekering)
- **Overige extra's** - 3 velden voor vaste bedragen in euro's (bijv. bonus, vergoeding)
- **Betaalfrequentie** - Kies tussen maandelijks of 4-wekelijks betaald
- **Live preview** - Overzicht updatet direct terwijl je invoert

### Werktijd Calculator
- Voer een bedrag in en zie hoe lang je ervoor moet werken
- **Netto werktijd** - Gebaseerd op je netto-urloon (na belasting)
- **Bruto werktijd** - Gebaseerd op je bruto-urloon (voor belasting)
- Vergelijking met dagelijkse uitgaven (koffie, lunches, pizza's)
- **Sparen vs Beleggen** - Wat het bedrag waard zou zijn tot je pensioen

### Sparen Simulator
- Bereken hoe je spaargeld groeit met rente
- Spaardoel calculator - hoelang duurt het om je doel te bereiken
- Jaarlijks overzicht met rente-op-rente effect

### Beleggen Simulator
- Projectie van je beleggingen met verwacht rendement
- **Nominaal vs Reëel** - Verschil tussen daadwerkelijk bedrag en koopkracht
- Vergelijking met sparen (1,3% rente)
- Interactieve grafiek met nominaal, reëel en ingelegd bedrag

### Overig
- **12 valuta's** - EUR, USD, GBP, SEK, NOK, DKK, CHF, PLN, TRY, BRL, INR, JPY
- **6 belastingregio's** - Nederland, België, Duitsland, VK, Zweden + aangepast
- **Pensioen info** - Jaren tot pensioen op alle schermen
- **Bottom navigatie** - Home, Salaris, Werktijd, Sparen, Beleggen
- **Logo** - Eigen MoneyWise launcher icon

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- ViewModel + StateFlow
- DataStore Preferences (lokale opslag)
- Navigation Compose
- kotlinx.serialization

## Aan de slag

Open in Android Studio en draai op emulator of apparaat (minSdk 26+).

```bash
./gradlew assembleDebug
```

## Projectstructuur

```
app/src/main/java/com/moneywise/
├── data/
│   ├── SalaryProfile.kt      Salarismodel met alle velden
│   ├── TaxConfig.kt           Belastingberekening per regio
│   ├── Calculators.kt         Werktijd, spaar & beleggingsberekeningen
│   └── Currency.kt            12 valuta's
├── viewmodel/
│   └── SalaryViewModel.kt     State management + DataStore
├── ui/
│   ├── theme/                 Material 3 thema
│   └── screens/
│       ├── HomeScreen.kt      Dashboard met overzicht
│       ├── SalaryInputScreen.kt   Salaris invoer + preview
│       ├── WorkTimeScreen.kt      Werktijd calculator
│       ├── SavingsScreen.kt       Spaarsimulator
│       └── InvestmentScreen.kt    Beleggingssimulator
└── MainActivity.kt           Entry point + Navigation
```

## Tests

```bash
./gradlew test
```

21 unit tests voor belastingberekening, salarisberekening en calculators.

## License

MIT
