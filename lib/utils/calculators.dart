import '../models/salary_profile.dart';

class WorkTimeResult {
  final double hours;
  final double minutes;
  final double grossHours;
  final double grossMinutes;
  final double costGross;
  final double costNet;
  final String humanReadable;
  final String humanReadableGross;

  const WorkTimeResult({
    required this.hours,
    required this.minutes,
    required this.grossHours,
    required this.grossMinutes,
    required this.costGross,
    required this.costNet,
    required this.humanReadable,
    required this.humanReadableGross,
  });
}

class SavingsResult {
  final double initialAmount;
  final double monthlyContribution;
  final double annualRate;
  final int years;
  final List<SavingsYear> timeline;

  const SavingsResult({
    required this.initialAmount,
    required this.monthlyContribution,
    required this.annualRate,
    required this.years,
    required this.timeline,
  });

  double get finalAmount => timeline.isNotEmpty ? timeline.last.totalBalance : initialAmount;
  double get totalContributed => initialAmount + (monthlyContribution * years * 12);
  double get totalInterest => finalAmount - totalContributed;
}

class SavingsYear {
  final int year;
  final double totalBalance;
  final double contributed;
  final double interestEarned;

  const SavingsYear({
    required this.year,
    required this.totalBalance,
    required this.contributed,
    required this.interestEarned,
  });
}

class InvestmentResult {
  final double initialAmount;
  final double monthlyContribution;
  final double annualReturn;
  final int years;
  final double inflationRate;
  final List<InvestmentYear> timeline;

  const InvestmentResult({
    required this.initialAmount,
    required this.monthlyContribution,
    required this.annualReturn,
    required this.years,
    required this.inflationRate,
    required this.timeline,
  });

  double get finalAmount => timeline.isNotEmpty ? timeline.last.nominalBalance : initialAmount;
  double get finalRealAmount => timeline.isNotEmpty ? timeline.last.realBalance : initialAmount;
  double get totalContributed => initialAmount + (monthlyContribution * years * 12);
  double get totalGain => finalAmount - totalContributed;
  double get totalRealGain => finalRealAmount - totalContributed;
}

class InvestmentYear {
  final int year;
  final double nominalBalance;
  final double realBalance;
  final double contributed;

  const InvestmentYear({
    required this.year,
    required this.nominalBalance,
    required this.realBalance,
    required this.contributed,
  });
}

class Calculators {
  static WorkTimeResult calculateWorkTime({
    required double purchasePrice,
    required SalaryProfile profile,
  }) {
    final grossHours = purchasePrice / profile.hourlyWage;
    final netHours = purchasePrice / profile.netHourly;

    final grossH = grossHours.floor();
    final grossM = ((grossHours - grossH) * 60).round();
    final netH = netHours.floor();
    final netM = ((netHours - netH) * 60).round();

    String humanReadable;
    if (netHours < 1) {
      humanReadable = '${netM.round()} minuten';
    } else if (netHours < 24) {
      humanReadable = '$netH uur en $netM minuten';
    } else {
      final days = (netHours / (profile.hoursPerWeek / 5)).toStringAsFixed(1);
      humanReadable = '$days werkdagen';
    }

    String humanReadableGross;
    if (grossHours < 1) {
      humanReadableGross = '${grossM.round()} minuten';
    } else if (grossHours < 24) {
      humanReadableGross = '$grossH uur en $grossM minuten';
    } else {
      final days = (grossHours / (profile.hoursPerWeek / 5)).toStringAsFixed(1);
      humanReadableGross = '$days werkdagen';
    }

    return WorkTimeResult(
      hours: netHours,
      minutes: netM.toDouble(),
      grossHours: grossHours,
      grossMinutes: grossM.toDouble(),
      costGross: purchasePrice / profile.hourlyWage * profile.hourlyWage,
      costNet: purchasePrice,
      humanReadable: humanReadable,
      humanReadableGross: humanReadableGross,
    );
  }

  static SavingsResult calculateSavings({
    required double initialAmount,
    required double monthlyContribution,
    required double annualRate,
    required int years,
  }) {
    final monthlyRate = annualRate / 100 / 12;
    final timeline = <SavingsYear>[];
    double balance = initialAmount;
    double totalContributed = initialAmount;

    for (int year = 1; year <= years; year++) {
      for (int month = 0; month < 12; month++) {
        balance += monthlyContribution;
        balance *= (1 + monthlyRate);
        totalContributed += monthlyContribution;
      }
      timeline.add(SavingsYear(
        year: year,
        totalBalance: balance,
        contributed: totalContributed,
        interestEarned: balance - totalContributed,
      ));
    }

    return SavingsResult(
      initialAmount: initialAmount,
      monthlyContribution: monthlyContribution,
      annualRate: annualRate,
      years: years,
      timeline: timeline,
    );
  }

  static InvestmentResult calculateInvestment({
    required double initialAmount,
    required double monthlyContribution,
    required double annualReturn,
    required int years,
    double inflationRate = 2.5,
  }) {
    final monthlyReturn = annualReturn / 100 / 12;
    final monthlyInflation = inflationRate / 100 / 12;
    final timeline = <InvestmentYear>[];
    double nominalBalance = initialAmount;
    double realBalance = initialAmount;
    double totalContributed = initialAmount;

    for (int year = 1; year <= years; year++) {
      for (int month = 0; month < 12; month++) {
        nominalBalance += monthlyContribution;
        nominalBalance *= (1 + monthlyReturn);
        realBalance += monthlyContribution;
        realBalance *= (1 + monthlyReturn - monthlyInflation);
        totalContributed += monthlyContribution;
      }
      timeline.add(InvestmentYear(
        year: year,
        nominalBalance: nominalBalance,
        realBalance: realBalance,
        contributed: totalContributed,
      ));
    }

    return InvestmentResult(
      initialAmount: initialAmount,
      monthlyContribution: monthlyContribution,
      annualReturn: annualReturn,
      years: years,
      inflationRate: inflationRate,
      timeline: timeline,
    );
  }

  static String formatWorkTime(double totalHours) {
    final h = totalHours.floor();
    final m = ((totalHours - h) * 60).round();
    if (h == 0) return '$m min';
    if (m == 0) return '$h uur';
    return '$h uur en $m min';
  }
}
