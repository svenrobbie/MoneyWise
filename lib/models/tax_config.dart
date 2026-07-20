class TaxBracket {
  final double from;
  final double to;
  final double rate;

  const TaxBracket({
    required this.from,
    this.to = double.infinity,
    required this.rate,
  });

  double get midpoint => to == double.infinity ? from : (from + to) / 2;
}

class TaxConfig {
  final String name;
  final String country;
  final List<TaxBracket> brackets;
  final double socialSecurityRate;
  final double generalTaxCredit;
  final double employmentTaxCredit;

  const TaxConfig({
    required this.name,
    required this.country,
    required this.brackets,
    this.socialSecurityRate = 0.0,
    this.generalTaxCredit = 0.0,
    this.employmentTaxCredit = 0.0,
  });

  double calculateTax(double annualIncome) {
    double tax = 0.0;
    for (final bracket in brackets) {
      if (annualIncome <= bracket.from) break;
      final taxableInBracket = annualIncome > bracket.to
          ? bracket.to - bracket.from
          : annualIncome - bracket.from;
      tax += taxableInBracket * bracket.rate;
    }
    return tax - generalTaxCredit - employmentTaxCredit;
  }

  double calculateEffectiveRate(double annualIncome) {
    if (annualIncome <= 0) return 0.0;
    return calculateTax(annualIncome) / annualIncome;
  }

  double calculateNetMonthly(double annualIncome) {
    final annualTax = calculateTax(annualIncome);
    final socialSecurity = annualIncome * socialSecurityRate;
    return (annualIncome - annualTax - socialSecurity) / 12;
  }

  double calculateGrossFromNet(double netMonthly) {
    double low = 0;
    double high = netMonthly * 12 * 3;
    for (int i = 0; i < 50; i++) {
      final mid = (low + high) / 2;
      final net = calculateNetMonthly(mid);
      if (net < netMonthly) {
        low = mid;
      } else {
        high = mid;
      }
    }
    return (low + high) / 2;
  }

  static const netherlands = TaxConfig(
    name: 'Nederland (2025)',
    country: 'NL',
    brackets: [
      TaxBracket(from: 0, to: 75518, rate: 0.3693),
      TaxBracket(from: 75518, rate: 0.4950),
    ],
    socialSecurityRate: 0.0,
    generalTaxCredit: 3362,
    employmentTaxCredit: 5532,
  );

  static const belgium = TaxConfig(
    name: 'Belgie (2025)',
    country: 'BE',
    brackets: [
      TaxBracket(from: 0, to: 15820, rate: 0.25),
      TaxBracket(from: 15820, to: 27920, rate: 0.40),
      TaxBracket(from: 27920, to: 48320, rate: 0.45),
      TaxBracket(from: 48320, rate: 0.50),
    ],
    socialSecurityRate: 0.1307,
    generalTaxCredit: 0,
    employmentTaxCredit: 0,
  );

  static const germany = TaxConfig(
    name: 'Duitsland (2025)',
    country: 'DE',
    brackets: [
      TaxBracket(from: 0, to: 12096, rate: 0.0),
      TaxBracket(from: 12096, to: 17005, rate: 0.14),
      TaxBracket(from: 17005, to: 66760, rate: 0.24),
      TaxBracket(from: 66760, to: 277825, rate: 0.42),
      TaxBracket(from: 277825, rate: 0.45),
    ],
    socialSecurityRate: 0.203,
    generalTaxCredit: 0,
    employmentTaxCredit: 0,
  );

  static const uk = TaxConfig(
    name: 'Verenigd Koninkrijk (2025)',
    country: 'UK',
    brackets: [
      TaxBracket(from: 0, to: 12570, rate: 0.0),
      TaxBracket(from: 12570, to: 50270, rate: 0.20),
      TaxBracket(from: 50270, to: 125140, rate: 0.40),
      TaxBracket(from: 125140, rate: 0.45),
    ],
    socialSecurityRate: 0.08,
    generalTaxCredit: 0,
    employmentTaxCredit: 0,
  );

  static const sweden = TaxConfig(
    name: 'Zweden (2025)',
    country: 'SE',
    brackets: [
      TaxBracket(from: 0, to: 598500, rate: 0.30),
      TaxBracket(from: 598500, rate: 0.52),
    ],
    socialSecurityRate: 0.07,
    generalTaxCredit: 0,
    employmentTaxCredit: 0,
  );

  static const custom = TaxConfig(
    name: 'Aangepast',
    country: 'XX',
    brackets: [
      TaxBracket(from: 0, rate: 0.25),
    ],
    socialSecurityRate: 0.0,
    generalTaxCredit: 0,
    employmentTaxCredit: 0,
  );

  static const all = [netherlands, belgium, germany, uk, sweden, custom];

  Map<String, dynamic> toJson() => {
    'name': name,
    'country': country,
    'brackets': brackets.map((b) => {
      'from': b.from,
      'to': b.to == double.infinity ? null : b.to,
      'rate': b.rate,
    }).toList(),
    'socialSecurityRate': socialSecurityRate,
    'generalTaxCredit': generalTaxCredit,
    'employmentTaxCredit': employmentTaxCredit,
  };

  factory TaxConfig.fromJson(Map<String, dynamic> json) => TaxConfig(
    name: json['name'] as String,
    country: json['country'] as String,
    brackets: (json['brackets'] as List).map((b) => TaxBracket(
      from: (b['from'] as num).toDouble(),
      to: b['to'] != null ? (b['to'] as num).toDouble() : double.infinity,
      rate: (b['rate'] as num).toDouble(),
    )).toList(),
    socialSecurityRate: (json['socialSecurityRate'] as num?)?.toDouble() ?? 0.0,
    generalTaxCredit: (json['generalTaxCredit'] as num?)?.toDouble() ?? 0.0,
    employmentTaxCredit: (json['employmentTaxCredit'] as num?)?.toDouble() ?? 0.0,
  );
}
