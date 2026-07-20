class Currency {
  final String code;
  final String symbol;
  final String name;
  final int decimalPlaces;

  const Currency({
    required this.code,
    required this.symbol,
    required this.name,
    this.decimalPlaces = 2,
  });

  static const eur = Currency(code: 'EUR', symbol: '\u20ac', name: 'Euro');
  static const usd = Currency(code: 'USD', symbol: '\$', name: 'US Dollar');
  static const gbp = Currency(code: 'GBP', symbol: '\u00a3', name: 'British Pound');
  static const sek = Currency(code: 'SEK', symbol: 'kr', name: 'Swedish Krona', decimalPlaces: 0);
  static const nok = Currency(code: 'NOK', symbol: 'kr', name: 'Norwegian Krone', decimalPlaces: 0);
  static const dkk = Currency(code: 'DKK', symbol: 'kr', name: 'Danish Krone');
  static const chf = Currency(code: 'CHF', symbol: 'CHF', name: 'Swiss Franc');
  static const pln = Currency(code: 'PLN', symbol: 'z\u0142', name: 'Polish Zloty');
  static const try_ = Currency(code: 'TRY', symbol: '\u20ba', name: 'Turkish Lira', decimalPlaces: 0);
  static const brl = Currency(code: 'BRL', symbol: 'R\$', name: 'Brazilian Real');
  static const inr = Currency(code: 'INR', symbol: '\u20b9', name: 'Indian Rupee', decimalPlaces: 0);
  static const jpy = Currency(code: 'JPY', symbol: '\u00a5', name: 'Japanese Yen', decimalPlaces: 0);

  static const all = [eur, usd, gbp, sek, nok, dkk, chf, pln, try_, brl, inr, jpy];

  static Currency fromCode(String code) {
    return all.firstWhere(
      (c) => c.code == code,
      orElse: () => eur,
    );
  }

  Map<String, dynamic> toJson() => {
    'code': code,
    'symbol': symbol,
    'name': name,
    'decimalPlaces': decimalPlaces,
  };

  factory Currency.fromJson(Map<String, dynamic> json) => Currency(
    code: json['code'] as String,
    symbol: json['symbol'] as String,
    name: json['name'] as String,
    decimalPlaces: json['decimalPlaces'] as int? ?? 2,
  );

  @override
  bool operator ==(Object other) =>
      identical(this, other) || other is Currency && code == other.code;

  @override
  int get hashCode => code.hashCode;

  @override
  String toString() => '$code ($symbol)';
}
