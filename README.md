# MoneyWise

Smart salary calculator - see what your money really costs in work time.

## Features

- **Salary Input** - Enter your hourly wage or salary with all extras (holiday allowance, overtime, night/weekend/shift premiums, end-of-year bonus)
- **Work Time Calculator** - See how long you need to work for any purchase (net & gross)
- **Savings Calculator** - Visualize how your savings grow over time with compound interest
- **Investment Calculator** - Project your investment growth with inflation-adjusted returns
- **Multiple Currencies** - EUR, USD, GBP, SEK, NOK, DKK, CHF, PLN, TRY, BRL, INR, JPY
- **Configurable Tax Rates** - Pre-configured for Netherlands, Belgium, Germany, UK, Sweden + custom option
- **Material 3 Dynamic Theming** - Adapts to your device's color scheme

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- ViewModel + StateFlow
- DataStore Preferences (local storage)
- Navigation Compose
- Vico (charts)

## Getting Started

Open in Android Studio and run on emulator or device (minSdk 26+).

```bash
./gradlew assembleDebug
```

## Project Structure

```
app/src/main/java/com/moneywise/
├── data/            Data models & calculators
├── viewmodel/       ViewModel for state management
├── ui/
│   ├── theme/       Material 3 theme
│   └── screens/     App screens
└── MainActivity.kt  Entry point + Navigation
```

## License

MIT
