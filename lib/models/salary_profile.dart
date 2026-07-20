import 'currency.dart';
import 'tax_config.dart';

class SalaryProfile {
  final double hourlyWage;
  final double hoursPerWeek;
  final double holidayAllowancePercent;
  final double endOfYearBonusPercent;
  final double overtimePercent;
  final double weekendPercent;
  final double nightPercent;
  final double shiftPercent;
  final TaxConfig taxConfig;
  final Currency currency;

  const SalaryProfile({
    required this.hourlyWage,
    this.hoursPerWeek = 40,
    this.holidayAllowancePercent = 8.0,
    this.endOfYearBonusPercent = 0.0,
    this.overtimePercent = 0.0,
    this.weekendPercent = 0.0,
    this.nightPercent = 0.0,
    this.shiftPercent = 0.0,
    this.taxConfig = TaxConfig.netherlands,
    this.currency = Currency.eur,
  });

  double get weeklyGross => hourlyWage * hoursPerWeek;
  double get annualGross => weeklyGross * 52;
  double get annualWithHolidayAllowance => annualGross * (1 + holidayAllowancePercent / 100);
  double get annualWithBonus => annualWithHolidayAllowance * (1 + endOfYearBonusPercent / 100);
  double get totalAnnualGross => annualWithBonus;

  double get netMonthly => taxConfig.calculateNetMonthly(totalAnnualGross);
  double get netAnnual => netMonthly * 12;
  double get netHourly => netAnnual / (hoursPerWeek * 52);
  double get effectiveTaxRate => taxConfig.calculateEffectiveRate(totalAnnualGross);

  double overtimeHourly(double percent) => hourlyWage * (1 + percent / 100);

  double get monthlyGross => totalAnnualGross / 12;

  SalaryProfile copyWith({
    double? hourlyWage,
    double? hoursPerWeek,
    double? holidayAllowancePercent,
    double? endOfYearBonusPercent,
    double? overtimePercent,
    double? weekendPercent,
    double? nightPercent,
    double? shiftPercent,
    TaxConfig? taxConfig,
    Currency? currency,
  }) {
    return SalaryProfile(
      hourlyWage: hourlyWage ?? this.hourlyWage,
      hoursPerWeek: hoursPerWeek ?? this.hoursPerWeek,
      holidayAllowancePercent: holidayAllowancePercent ?? this.holidayAllowancePercent,
      endOfYearBonusPercent: endOfYearBonusPercent ?? this.endOfYearBonusPercent,
      overtimePercent: overtimePercent ?? this.overtimePercent,
      weekendPercent: weekendPercent ?? this.weekendPercent,
      nightPercent: nightPercent ?? this.nightPercent,
      shiftPercent: shiftPercent ?? this.shiftPercent,
      taxConfig: taxConfig ?? this.taxConfig,
      currency: currency ?? this.currency,
    );
  }

  Map<String, dynamic> toJson() => {
    'hourlyWage': hourlyWage,
    'hoursPerWeek': hoursPerWeek,
    'holidayAllowancePercent': holidayAllowancePercent,
    'endOfYearBonusPercent': endOfYearBonusPercent,
    'overtimePercent': overtimePercent,
    'weekendPercent': weekendPercent,
    'nightPercent': nightPercent,
    'shiftPercent': shiftPercent,
    'taxConfig': taxConfig.toJson(),
    'currency': currency.toJson(),
  };

  factory SalaryProfile.fromJson(Map<String, dynamic> json) => SalaryProfile(
    hourlyWage: (json['hourlyWage'] as num).toDouble(),
    hoursPerWeek: (json['hoursPerWeek'] as num?)?.toDouble() ?? 40,
    holidayAllowancePercent: (json['holidayAllowancePercent'] as num?)?.toDouble() ?? 8.0,
    endOfYearBonusPercent: (json['endOfYearBonusPercent'] as num?)?.toDouble() ?? 0.0,
    overtimePercent: (json['overtimePercent'] as num?)?.toDouble() ?? 0.0,
    weekendPercent: (json['weekendPercent'] as num?)?.toDouble() ?? 0.0,
    nightPercent: (json['nightPercent'] as num?)?.toDouble() ?? 0.0,
    shiftPercent: (json['shiftPercent'] as num?)?.toDouble() ?? 0.0,
    taxConfig: json['taxConfig'] != null
        ? TaxConfig.fromJson(json['taxConfig'] as Map<String, dynamic>)
        : TaxConfig.netherlands,
    currency: json['currency'] != null
        ? Currency.fromJson(json['currency'] as Map<String, dynamic>)
        : Currency.eur,
  );

  factory SalaryProfile.defaultProfile() => const SalaryProfile(hourlyWage: 16.00);
}
