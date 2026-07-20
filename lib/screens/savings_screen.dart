import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:fl_chart/fl_chart.dart';
import '../models/currency.dart';
import '../providers/salary_provider.dart';
import '../utils/calculators.dart';
import '../utils/constants.dart';
import '../utils/currency_formatter.dart';

class SavingsScreen extends ConsumerStatefulWidget {
  const SavingsScreen({super.key});

  @override
  ConsumerState<SavingsScreen> createState() => _SavingsScreenState();
}

class _SavingsScreenState extends ConsumerState<SavingsScreen> {
  late final TextEditingController _initialController;
  late final TextEditingController _monthlyController;
  late final TextEditingController _rateController;
  int _years = AppConstants.defaultProjectionYears;
  late SavingsResult _result;

  @override
  void initState() {
    super.initState();
    _initialController = TextEditingController(text: '0');
    _monthlyController = TextEditingController(text: '0');
    _rateController = TextEditingController(
      text: AppConstants.defaultSavingsRate.toString(),
    );
    _result = Calculators.calculateSavings(
      initialAmount: 0,
      monthlyContribution: 0,
      annualRate: AppConstants.defaultSavingsRate,
      years: _years,
    );
  }

  @override
  void dispose() {
    _initialController.dispose();
    _monthlyController.dispose();
    _rateController.dispose();
    super.dispose();
  }

  void _recalculate() {
    final initial = double.tryParse(_initialController.text) ?? 0;
    final monthly = double.tryParse(_monthlyController.text) ?? 0;
    final rate = double.tryParse(_rateController.text) ?? 0;
    setState(() {
      _result = Calculators.calculateSavings(
        initialAmount: initial,
        monthlyContribution: monthly,
        annualRate: rate,
        years: _years,
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    final profile = ref.watch(salaryProvider);
    final currency = profile.currency;
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(title: const Text('Sparen')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildInputSection(theme),
          const SizedBox(height: 24),
          _buildResultCards(theme, colorScheme, currency),
          const SizedBox(height: 24),
          _buildChart(theme, colorScheme, currency),
          const SizedBox(height: 24),
          _buildTimeline(theme, colorScheme, currency),
        ],
      ),
    );
  }

  Widget _buildInputSection(ThemeData theme) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Invoer',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _initialController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Bedrag',
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: (_) => _recalculate(),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _monthlyController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Maandelijkse inleg',
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: (_) => _recalculate(),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _rateController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Jaarlijks rentepercentage (%)',
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: (_) => _recalculate(),
            ),
            const SizedBox(height: 16),
            Text(
              'Jaren: $_years',
              style: theme.textTheme.bodyMedium,
            ),
            Slider(
              value: _years.toDouble(),
              min: 1,
              max: AppConstants.maxProjectionYears.toDouble(),
              divisions: AppConstants.maxProjectionYears - 1,
              label: '$_years jaar',
              onChanged: (value) {
                setState(() => _years = value.round());
                _recalculate();
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildResultCards(ThemeData theme, ColorScheme colorScheme, Currency currency) {
    return Column(
      children: [
        _ResultCard(
          label: 'Eindsaldo',
          value: CurrencyFormatter.format(_result.finalAmount, currency),
          color: colorScheme.primaryContainer,
          foregroundColor: colorScheme.onPrimaryContainer,
        ),
        const SizedBox(height: 8),
        Row(
          children: [
            Expanded(
              child: _ResultCard(
                label: 'Totaal ingelegd',
                value: CurrencyFormatter.format(
                  _result.totalContributed,
                  currency,
                ),
                color: colorScheme.secondaryContainer,
                foregroundColor: colorScheme.onSecondaryContainer,
                compact: true,
              ),
            ),
            const SizedBox(width: 8),
            Expanded(
              child: _ResultCard(
                label: 'Rente verdiend',
                value: CurrencyFormatter.format(
                  _result.totalInterest,
                  currency,
                ),
                color: colorScheme.tertiaryContainer,
                foregroundColor: colorScheme.onTertiaryContainer,
                compact: true,
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildChart(ThemeData theme, ColorScheme colorScheme, Currency currency) {
    if (_result.timeline.isEmpty) return const SizedBox.shrink();

    final balanceSpots = _result.timeline
        .map((y) => FlSpot(y.year.toDouble(), y.totalBalance))
        .toList();
    final contributedSpots = _result.timeline
        .map((y) => FlSpot(y.year.toDouble(), y.contributed))
        .toList();

    final maxY = _result.finalAmount;

    return Card(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(8, 16, 16, 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.only(left: 16),
              child: Text(
                'Grafiek',
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 220,
              child: LineChart(
                LineChartData(
                  minY: 0,
                  maxY: maxY > 0 ? maxY * 1.05 : 1000,
                  minX: 0,
                  maxX: _result.years.toDouble() + 0.5,
                  lineTouchData: LineTouchData(
                    touchTooltipData: LineTouchTooltipData(
                      getTooltipItems: (spots) {
                        return spots.map((spot) {
                          final label = spot.seriesIndex == 0 ? 'Saldo' : 'Ingelegd';
                          return LineTooltipItem(
                            '$label\n${CurrencyFormatter.format(spot.y, currency, showDecimals: false)}',
                            TextStyle(
                              color: colorScheme.onSurface,
                              fontSize: 12,
                            ),
                          );
                        }).toList();
                      },
                    ),
                  ),
                  gridData: FlGridData(
                    show: true,
                    drawVerticalLine: false,
                    horizontalInterval: _gridInterval(maxY),
                    getDrawingHorizontalLine: (value) => FlLine(
                      color: colorScheme.outlineVariant.withValues(alpha: 0.3),
                      strokeWidth: 1,
                    ),
                  ),
                  titlesData: FlTitlesData(
                    leftTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        reservedSize: 48,
                        getTitlesWidget: (value, meta) {
                          return Text(
                            _formatAxisLabel(value),
                            style: theme.textTheme.bodySmall?.copyWith(
                              color: colorScheme.onSurfaceVariant,
                            ),
                          );
                        },
                      ),
                    ),
                    bottomTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        interval: _bottomInterval(_result.years),
                        getTitlesWidget: (value, meta) {
                          final year = value.round();
                          if (year == 0 || year > _result.years) {
                            return const SizedBox.shrink();
                          }
                          return Padding(
                            padding: const EdgeInsets.only(top: 4),
                            child: Text(
                              '$year',
                              style: theme.textTheme.bodySmall?.copyWith(
                                color: colorScheme.onSurfaceVariant,
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                    topTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    rightTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                  ),
                  borderData: FlBorderData(show: false),
                  lineBarsData: [
                    LineChartBarData(
                      spots: balanceSpots,
                      isCurved: true,
                      color: colorScheme.primary,
                      barWidth: 3,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: colorScheme.primary.withValues(alpha: 0.1),
                      ),
                    ),
                    LineChartBarData(
                      spots: contributedSpots,
                      isCurved: true,
                      color: colorScheme.secondary,
                      barWidth: 2,
                      dotData: const FlDotData(show: false),
                      dashArray: [6, 4],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _LegendDot(color: colorScheme.primary, label: 'Saldo'),
                const SizedBox(width: 24),
                _LegendDash(color: colorScheme.secondary, label: 'Ingelegd'),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTimeline(
    ThemeData theme,
    ColorScheme colorScheme,
    Currency currency,
  ) {
    final milestones = <SavingsYear>[];
    for (final year in _result.timeline) {
      if (year.year == 1 ||
          year.year == _result.years ||
          year.year % 5 == 0) {
        milestones.add(year);
      }
    }

    if (milestones.isEmpty) return const SizedBox.shrink();

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Tijdlijn',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            Table(
              columnWidths: const {
                0: FlexColumnWidth(1),
                1: FlexColumnWidth(1.5),
                2: FlexColumnWidth(1.5),
                3: FlexColumnWidth(1.5),
              },
              children: [
                TableRow(
                  children: [
                    _tableHeader('Jaar', theme, colorScheme),
                    _tableHeader('Saldo', theme, colorScheme),
                    _tableHeader('Ingelegd', theme, colorScheme),
                    _tableHeader('Rente', theme, colorScheme),
                  ],
                ),
                ...milestones.map(
                  (year) => TableRow(
                    decoration: BoxDecoration(
                      border: Border(
                        bottom: BorderSide(
                          color: colorScheme.outlineVariant
                              .withValues(alpha: 0.2),
                        ),
                      ),
                    ),
                    children: [
                      _tableCell('${year.year}', theme, bold: true),
                      _tableCell(
                        CurrencyFormatter.format(
                          year.totalBalance,
                          currency,
                          showDecimals: false,
                        ),
                        theme,
                      ),
                      _tableCell(
                        CurrencyFormatter.format(
                          year.contributed,
                          currency,
                          showDecimals: false,
                        ),
                        theme,
                      ),
                      _tableCell(
                        CurrencyFormatter.format(
                          year.interestEarned,
                          currency,
                          showDecimals: false,
                        ),
                        theme,
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _tableHeader(
    String text,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Text(
        text,
        style: theme.textTheme.labelMedium?.copyWith(
          color: colorScheme.onSurfaceVariant,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  Widget _tableCell(String text, ThemeData theme, {bool bold = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Text(
        text,
        style: theme.textTheme.bodySmall?.copyWith(
          fontWeight: bold ? FontWeight.w600 : FontWeight.normal,
        ),
      ),
    );
  }

  double _gridInterval(double maxY) {
    if (maxY <= 0) return 1000;
    final raw = maxY / 5;
    final magnitude = _pow(3, (raw / 3).floor().toDouble());
    final normalized = raw / magnitude;
    double nice;
    if (normalized <= 1) {
      nice = 1;
    } else if (normalized <= 2) {
      nice = 2;
    } else if (normalized <= 5) {
      nice = 5;
    } else {
      nice = 10;
    }
    return nice * magnitude;
  }

  double _pow(double base, double exp) {
    var result = 1.0;
    final e = exp.round();
    for (var i = 0; i < e.abs(); i++) {
      result *= base;
    }
    return e < 0 ? 1 / result : result;
  }

  String _formatAxisLabel(double value) {
    if (value >= 1000000) {
      return '${(value / 1000000).toStringAsFixed(1)}M';
    } else if (value >= 1000) {
      return '${(value / 1000).toStringAsFixed(0)}K';
    }
    return value.toStringAsFixed(0);
  }

  double _bottomInterval(int years) {
    if (years <= 10) return 1;
    if (years <= 25) return 5;
    return 10;
  }
}

class _ResultCard extends StatelessWidget {
  final String label;
  final String value;
  final Color color;
  final Color foregroundColor;
  final bool compact;

  const _ResultCard({
    required this.label,
    required this.value,
    required this.color,
    required this.foregroundColor,
    this.compact = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      color: color,
      child: Padding(
        padding: EdgeInsets.all(compact ? 12 : 16),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    label,
                    style: theme.textTheme.labelMedium?.copyWith(
                      color: foregroundColor.withValues(alpha: 0.7),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    value,
                    style: (compact
                            ? theme.textTheme.titleMedium
                            : theme.textTheme.headlineSmall)
                        ?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: foregroundColor,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _LegendDot extends StatelessWidget {
  final Color color;
  final String label;

  const _LegendDot({required this.color, required this.label});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 12,
          height: 12,
          decoration: BoxDecoration(
            color: color,
            shape: BoxShape.circle,
          ),
        ),
        const SizedBox(width: 6),
        Text(label, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }
}

class _LegendDash extends StatelessWidget {
  final Color color;
  final String label;

  const _LegendDash({required this.color, required this.label});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        CustomPaint(
          size: const Size(12, 2),
          painter: _DashedLinePainter(color: color),
        ),
        const SizedBox(width: 6),
        Text(label, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }
}

class _DashedLinePainter extends CustomPainter {
  final Color color;

  const _DashedLinePainter({required this.color});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..strokeWidth = 2
      ..style = PaintingStyle.stroke;
    const dashWidth = 3.0;
    const dashSpace = 2.0;
    double x = 0;
    while (x < size.width) {
      canvas.drawLine(
        Offset(x, 0),
        Offset(x + dashWidth, 0),
        paint,
      );
      x += dashWidth + dashSpace;
    }
  }

  @override
  bool shouldRepaint(covariant _DashedLinePainter old) => old.color != color;
}
