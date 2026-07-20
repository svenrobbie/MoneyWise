import 'package:intl/intl.dart';
import '../models/currency.dart';

class CurrencyFormatter {
  static String format(double amount, Currency currency, {bool showDecimals = true}) {
    final formatter = NumberFormat.currency(
      symbol: currency.symbol,
      decimalDigits: showDecimals ? currency.decimalPlaces : 0,
    );
    return formatter.format(amount);
  }

  static String formatCompact(double amount, Currency currency) {
    if (amount.abs() >= 1000000) {
      return '${currency.symbol}${(amount / 1000000).toStringAsFixed(1)}M';
    } else if (amount.abs() >= 1000) {
      return '${currency.symbol}${(amount / 1000).toStringAsFixed(1)}K';
    }
    return format(amount, currency);
  }

  static String formatHourly(double amount, Currency currency) {
    return '${format(amount, currency)}/u';
  }

  static String formatMonthly(double amount, Currency currency) {
    return '${format(amount, currency)}/maand';
  }

  static String formatAnnual(double amount, Currency currency) {
    return '${format(amount, currency)}/jaar';
  }
}
