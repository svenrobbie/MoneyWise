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
- **Nominaal vs Reel** - Verschil tussen daadwerkelijk bedrag en koopkracht
- Vergelijking met sparen (1,3% rente)
- Interactieve grafiek met nominaal, reeel en ingelegd bedrag

### Portefeuille
- **Aandelen beheren** - Voeg aandelen/ETFs toe met symbool, naam, aantal en aankoopprijs
- **Bij kopen** - Koop extra eenheden, gemiddelde aankoopprijs wordt automatisch berekend
- **Verkopen** - Verkoop (een deel van) je aandelen, opbrengst gaat naar je portemonnee
- **Portemonnee** - Houd saldo bij op je exchange (storten / opnemen)
- **Doelverdeling** - Stel per aandeel een doelpercentage in
- **Rebalancing advies** - Bereken wat je moet kopen op basis van je maandbedrag en doelverdeling
- **Projectie** - Zie hoe je portefeuille groeit over X jaar bij diverse rendementen

### Advies
- **Doelverdeling calculator** - Bereken de optimale verdeling op basis van je salaris
- Visueel overzicht van aanbevolen allocatie

### Allocatie
- **Netto verdeling** - Visueel overzicht van hoe je netto-inkomen verdeeld is
- Overzicht van vaste lasten, sparen, beleggen en overig

### Data Beheer
- **Exporteren** - Sla je volledige portefeuille op als JSON-bestand
- **Importeren** - Laad een eerder geexporteerd bestand (met bevestigingsdialog)
- Bestandsformaat: JSON met versienummer en timestamp voor toekomstbestendigheid

### Notificaties
- **Herinnering** - Automatische dagelijkse melding als je je maandelijkse investering nog niet hebt gedaan

### Overig
- **12 valuta's** - EUR, USD, GBP, SEK, NOK, DKK, CHF, PLN, TRY, BRL, INR, JPY
- **6 belastingregio's** - Nederland, Belgie, Duitsland, VK, Zweden + aangepast
- **Pensioen info** - Jaren tot pensioen op alle schermen
- **Bottom navigatie** - Home, Salaris, Werktijd, Sparen, Beleggen
- **Logo** - Eigen MoneyWise launcher icon
- **Profile screen** - Over MoneyWise, GitHub link, data export/import

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- ViewModel + StateFlow
- DataStore Preferences (lokale opslag)
- Navigation Compose
- kotlinx.serialization
- WorkManager (notificaties)

## Aan de slag

Open in Android Studio en draai op emulator of apparaat (minSdk 26+).

```bash
./gradlew assembleDebug
```

### Release APK bouwen

```bash
./gradlew assembleRelease
```

De signed release APK staat in `app/build/outputs/apk/release/app-release.apk`.

## Projectstructuur

```
app/src/main/java/com/moneywise/
├── data/
│   ├── SalaryProfile.kt      Salarismodel met alle velden
│   ├── TaxConfig.kt           Belastingberekening per regio
│   ├── Portfolio.kt           Portefeuille model (aandelen + wallet)
│   ├── Calculators.kt         Werktijd, spaar & beleggingsberekeningen
│   └── Currency.kt            12 valuta's
├── viewmodel/
│   ├── SalaryViewModel.kt     State management + DataStore
│   └── PortfolioViewModel.kt  Portefeuille state + export/import
├── ui/
│   ├── theme/                 Material 3 thema
│   └── screens/
│       ├── HomeScreen.kt      Dashboard met overzicht
│       ├── SalaryInputScreen.kt   Salaris invoer + preview
│       ├── WorkTimeScreen.kt      Werktijd calculator
│       ├── SavingsScreen.kt       Spaarsimulator
│       ├── InvestmentScreen.kt    Beleggingssimulator
│       ├── PortfolioScreen.kt     Aandelen beheren + rebalancing
│       ├── AdviceScreen.kt        Doelverdeling advies
│       ├── AllocationScreen.kt    Visuele inkomstenverdeling
│       └── ProfileScreen.kt       Over MoneyWise + data export/import
├── worker/
│   ├── InvestmentReminderWorker.kt  Dagelijkse investering-herinnering
│   └── NotificationHelper.kt       Notificatie kanaal + tonen
└── MainActivity.kt           Entry point + Navigation
```

## Tests

```bash
./gradlew test
```

21 unit tests voor belastingberekening, salarisberekening en calculators.

## License

MIT
