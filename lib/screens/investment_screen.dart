import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:fl_chart/fl_chart.dart';
import '../providers/salary_provider.dart';
import '../utils/calculators.dart';
import '../utils/constants.dart';
import '../utils/currency_formatter.dart';

class InvestmentScreen extends ConsumerStatefulWidget {
  const InvestmentScreen({super.key});

  @override
  ConsumerState<InvestmentScreen> createState() => _InvestmentScreenState();
}

class _InvestmentScreenState extends ConsumerState<InvestmentScreen> {
  final _initialController = TextEditingController(text: '0');
  final _monthlyController = TextEditingController(text: '0');
  final _returnController =
      TextEditingController(text: AppConstants.defaultInvestmentReturn.toString());
  final _inflationController =
      TextEditingController(text: AppConstants.defaultInflationRate.toString());

  double _initial = 0;
  double _monthly = 0;
  double _annualReturn = AppConstants.defaultInvestmentReturn;
  double _inflationRate = AppConstants.defaultInflationRate;
  int _years = AppConstants.defaultProjectionYears;

  @override
  void dispose() {
    _initialController.dispose();
    _monthlyController.dispose();
    _returnController.dispose();
    _inflationController.dispose();
    super.dispose();
  }

  InvestmentResult _calculate() {
    return Calculators.calculateInvestment(
      initialAmount: _initial,
      monthlyContribution: _monthly,
      annualReturn: _annualReturn,
      years: _years,
      inflationRate: _inflationRate,
    );
  }

  void _parseAndSet(String value, void Function(double) setter, TextEditingController controller) {
    final parsed = double.tryParse(value.replaceAll(',', '.'));
    if (parsed != null) {
      setState(() => setter(parsed));
    }
  }

  @override
  Widget build(BuildContext context) {
    final profile = ref.watch(salaryProvider);
    final currency = profile.currency;
    final result = _calculate();
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final multiplier = result.totalContributed > 0
        ? result.finalAmount / result.totalContributed
        : 0.0;

    return Scaffold(
      appBar: AppBar(title: const Text('Beleggen')),
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
        children: [
          _InputSection(
            initialController: _initialController,
            monthlyController: _monthlyController,
            returnController: _returnController,
            inflationController: _inflationController,
            years: _years,
            onInitialChanged: (v) => _parseAndSet(v, (v) => _initial = v, _initialController),
            onMonthlyChanged: (v) => _parseAndSet(v, (v) => _monthly = v, _monthlyController),
            onReturnChanged: (v) => _parseAndSet(v, (v) => _annualReturn = v, _returnController),
            onInflationChanged: (v) =>
                _parseAndSet(v, (v) => _inflationRate = v, _inflationController),
            onYearsChanged: (v) => setState(() => _years = v),
          ),
          const SizedBox(height: 16),
          _ResultsSection(result: result, currency: currency, multiplier: multiplier),
          const SizedBox(height: 16),
          _ChartSection(result: result, colorScheme: colorScheme),
          const SizedBox(height: 16),
          _BreakdownSection(result: result, currency: currency),
        ],
      ),
    );
  }
}

class _InputSection extends StatelessWidget {
  final TextEditingController initialController;
  final TextEditingController monthlyController;
  final TextEditingController returnController;
  final TextEditingController inflationController;
  final int years;
  final ValueChanged<String> onInitialChanged;
  final ValueChanged<String> onMonthlyChanged;
  final ValueChanged<String> onReturnChanged;
  final ValueChanged<String> onInflationChanged;
  final ValueChanged<int> onYearsChanged;

  const _InputSection({
    required this.initialController,
    required this.monthlyController,
    required this.returnController,
    required this.inflationController,
    required this.years,
    required this.onInitialChanged,
    required this.onMonthlyChanged,
    required this.onReturnChanged,
    required this.onInflationChanged,
    required this.onYearsChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Instellingen',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                  ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: initialController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Startbedrag',
                prefixIcon: Icon(Icons.savings_outlined),
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: onInitialChanged,
            ),
            const SizedBox(height: 12),
            TextField(
              controller: monthlyController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Maandelijkse inleg',
                prefixIcon: Icon(Icons.calendar_month_outlined),
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: onMonthlyChanged,
            ),
            const SizedBox(height: 12),
            TextField(
              controller: returnController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Verwacht jaarlijks rendement (%)',
                prefixIcon: Icon(Icons.trending_up),
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: onReturnChanged,
            ),
            const SizedBox(height: 12),
            TextField(
              controller: inflationController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Inflatieratio (%)',
                prefixIcon: Icon(Icons.show_chart),
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: onInflationChanged,
            ),
            const SizedBox(height: 16),
            Text(
              'Beleggingsperiode: $years jaar',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.w500,
                  ),
            ),
            Slider(
              value: years.toDouble(),
              min: 1,
              max: AppConstants.maxProjectionYears.toDouble(),
              divisions: AppConstants.maxProjectionYears - 1,
              label: '$years jaar',
              onChanged: (v) => onYearsChanged(v.round()),
            ),
          ],
        ),
      ),
    );
  }
}

class _ResultsSection extends StatelessWidget {
  final InvestmentResult result;
  final dynamic currency;
  final double multiplier;

  const _ResultsSection({
    required this.result,
    required this.currency,
    required this.multiplier,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Resultaat',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            _ResultHighlight(
              label: 'Eindsaldo (nominaal)',
              value: CurrencyFormatter.format(result.finalAmount, currency),
              color: colorScheme.primary,
            ),
            const SizedBox(height: 12),
            _ResultHighlight(
              label: 'Eindsaldo (reëel)',
              value: CurrencyFormatter.format(result.finalRealAmount, currency),
              color: colorScheme.tertiary,
            ),
            const Divider(height: 24),
            _ResultRow(
              label: 'Totaal ingelegd',
              value: CurrencyFormatter.format(result.totalContributed, currency),
            ),
            const SizedBox(height: 8),
            _ResultRow(
              label: 'Nominaal rendement',
              value: CurrencyFormatter.format(result.totalGain, currency),
            ),
            const SizedBox(height: 8),
            _ResultRow(
              label: 'Reëel rendement',
              value: CurrencyFormatter.format(result.totalRealGain, currency),
            ),
            if (multiplier > 0) ...[
              const Divider(height: 24),
              Center(
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  decoration: BoxDecoration(
                    color: colorScheme.primaryContainer,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    'Dit is ${multiplier.toStringAsFixed(1)}x je inleg',
                    style: theme.textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: colorScheme.onPrimaryContainer,
                    ),
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _ResultHighlight extends StatelessWidget {
  final String label;
  final String value;
  final Color color;

  const _ResultHighlight({
    required this.label,
    required this.value,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: theme.textTheme.bodySmall?.copyWith(
            color: theme.colorScheme.onSurfaceVariant,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: theme.textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
      ],
    );
  }
}

class _ResultRow extends StatelessWidget {
  final String label;
  final String value;

  const _ResultRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: Theme.of(context).textTheme.bodyMedium),
        Text(
          value,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
        ),
      ],
    );
  }
}

class _ChartSection extends StatelessWidget {
  final InvestmentResult result;
  final ColorScheme colorScheme;

  const _ChartSection({required this.result, required this.colorScheme});

  @override
  Widget build(BuildContext context) {
    if (result.timeline.isEmpty) return const SizedBox.shrink();

    final nominalSpots = <FlSpot>[];
    final realSpots = <FlSpot>[];
    final contributedSpots = <FlSpot>[];

    for (final year in result.timeline) {
      final x = year.year.toDouble();
      nominalSpots.add(FlSpot(x, year.nominalBalance));
      realSpots.add(FlSpot(x, year.realBalance));
      contributedSpots.add(FlSpot(x, year.contributed));
    }

    final maxY = result.timeline.last.nominalBalance;

    return Card(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 20, 20, 12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.only(left: 8),
              child: Text(
                'Groei over tijd',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 240,
              child: LineChart(
                LineChartData(
                  gridData: FlGridData(
                    show: true,
                    drawVerticalLine: false,
                    horizontalInterval: _niceInterval(maxY),
                    getDrawingHorizontalLine: (value) => FlLine(
                      color: colorScheme.outlineVariant.withValues(alpha: 0.3),
                      strokeWidth: 1,
                    ),
                  ),
                  titlesData: FlTitlesData(
                    leftTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        reservedSize: 56,
                        getTitlesWidget: (value, meta) {
                          if (value == 0) return const SizedBox.shrink();
                          return Padding(
                            padding: const EdgeInsets.only(right: 8),
                            child: Text(
                              _compactLabel(value),
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                    color: colorScheme.onSurfaceVariant,
                                  ),
                            ),
                          );
                        },
                      ),
                    ),
                    bottomTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        interval: max(1, (result.timeline.length / 5).roundToDouble()),
                        getTitlesWidget: (value, meta) {
                          final year = value.toInt();
                          if (year <= 0 || year > result.years) return const SizedBox.shrink();
                          return Padding(
                            padding: const EdgeInsets.only(top: 8),
                            child: Text(
                              '$year',
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                    color: colorScheme.onSurfaceVariant,
                                  ),
                            ),
                          );
                        },
                      ),
                    ),
                    topTitles: const AxisTitles(sideTitles: SideTitles(showTitles: false)),
                    rightTitles: const AxisTitles(sideTitles: SideTitles(showTitles: false)),
                  ),
                  borderData: FlBorderData(show: false),
                  minX: 0,
                  maxX: result.years.toDouble(),
                  minY: 0,
                  maxY: maxY * 1.05,
                  lineBarsData: [
                    LineChartBarData(
                      spots: contributedSpots,
                      isCurved: false,
                      color: colorScheme.outlineVariant,
                      strokeWidth: 2,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: colorScheme.outlineVariant.withValues(alpha: 0.08),
                      ),
                    ),
                    LineChartBarData(
                      spots: realSpots,
                      isCurved: true,
                      color: colorScheme.tertiary,
                      strokeWidth: 2.5,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: colorScheme.tertiary.withValues(alpha: 0.08),
                      ),
                    ),
                    LineChartBarData(
                      spots: nominalSpots,
                      isCurved: true,
                      color: colorScheme.primary,
                      strokeWidth: 2.5,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: colorScheme.primary.withValues(alpha: 0.08),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 12),
            _Legend(
              items: [
                _LegendItem(color: colorScheme.primary, label: 'Nominaal'),
                _LegendItem(color: colorScheme.tertiary, label: 'Reëel'),
                _LegendItem(color: colorScheme.outlineVariant, label: 'Ingelegd'),
              ],
            ),
          ],
        ),
      ),
    );
  }

  double _niceInterval(double maxY) {
    if (maxY <= 0) return 1;
    final rough = maxY / 4;
    final magnitude = pow(10, (log(rough) / ln10).floor()).toDouble();
    final residual = rough / magnitude;
    if (residual <= 1) return magnitude;
    if (residual <= 2) return 2 * magnitude;
    if (residual <= 5) return 5 * magnitude;
    return 10 * magnitude;
  }

  String _compactLabel(double value) {
    if (value >= 1000000) return '${(value / 1000000).toStringAsFixed(1)}M';
    if (value >= 1000) return '${(value / 1000).toStringAsFixed(0)}K';
    return value.toInt().toString();
  }
}

class _LegendItem {
  final Color color;
  final String label;
  const _LegendItem({required this.color, required this.label});
}

class _Legend extends StatelessWidget {
  final List<_LegendItem> items;
  const _Legend({required this.items});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        for (final item in items) ...[
          Container(
            width: 12,
            height: 3,
            decoration: BoxDecoration(
              color: item.color,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(width: 6),
          Text(
            item.label,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
          ),
          const SizedBox(width: 16),
        ],
      ],
    );
  }
}

class _BreakdownSection extends StatelessWidget {
  final InvestmentResult result;
  final dynamic currency;

  const _BreakdownSection({required this.result, required this.currency});

  @override
  Widget build(BuildContext context) {
    if (result.timeline.isEmpty) return const SizedBox.shrink();

    final milestones = _pickMilestones(result);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Jaarlijks overzicht',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                  ),
            ),
            const SizedBox(height: 16),
            for (final year in milestones) ...[
              _MilestoneRow(
                year: year.year,
                nominal: year.nominalBalance,
                real: year.realBalance,
                contributed: year.contributed,
                currency: currency,
              ),
              if (year != milestones.last) const Divider(height: 20),
            ],
          ],
        ),
      ),
    );
  }

  List<InvestmentYear> _pickMilestones(InvestmentResult result) {
    final years = result.timeline;
    if (years.length <= 6) return years;

    final Set<int> indices = {0, years.length - 1};
    final targets = [5, 10, 15, 20, 25, 30, 40];
    for (final t in targets) {
      if (t <= years.length) indices.add(t - 1);
    }

    final sorted = indices.toList()..sort();
    return sorted.map((i) => years[i]).toList();
  }
}

class _MilestoneRow extends StatelessWidget {
  final int year;
  final double nominal;
  final double real;
  final double contributed;
  final dynamic currency;

  const _MilestoneRow({
    required this.year,
    required this.nominal,
    required this.real,
    required this.contributed,
    required this.currency,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                'Jaar $year',
                style: theme.textTheme.labelSmall?.copyWith(
                  fontWeight: FontWeight.w600,
                  color: colorScheme.onPrimaryContainer,
                ),
              ),
            ),
            const Spacer(),
            Text(
              CurrencyFormatter.format(nominal, currency),
              style: theme.textTheme.titleSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: colorScheme.primary,
              ),
            ),
          ],
        ),
        const SizedBox(height: 6),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Ingelegd: ${CurrencyFormatter.format(contributed, currency)}',
              style: theme.textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            Text(
              'Reëel: ${CurrencyFormatter.format(real, currency)}',
              style: theme.textTheme.bodySmall?.copyWith(
                color: colorScheme.tertiary,
                fontWeight: FontWeight.w500,
              ),
            ),
          ],
        ),
      ],
    );
  }
}
