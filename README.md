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

- Flutter + Dart
- Riverpod (state management)
- SharedPreferences (local storage)
- fl_chart (charts)
- Material 3 with dynamic color

## Getting Started

```bash
flutter pub get
flutter run
```

## Project Structure

```
lib/
├── models/          Data models (SalaryProfile, Currency, TaxConfig)
├── providers/       Riverpod state management
├── screens/         App screens (Home, Salary, WorkTime, Savings, Investment)
├── utils/           Calculators, formatters, constants
└── main.dart        Entry point
```

## License

MIT
