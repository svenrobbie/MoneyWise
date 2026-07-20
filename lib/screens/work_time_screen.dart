import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/salary_provider.dart';
import '../utils/calculators.dart';
import '../models/salary_profile.dart';
import '../utils/currency_formatter.dart';

class WorkTimeScreen extends ConsumerStatefulWidget {
  const WorkTimeScreen({super.key});

  @override
  ConsumerState<WorkTimeScreen> createState() => _WorkTimeScreenState();
}

class _WorkTimeScreenState extends ConsumerState<WorkTimeScreen> {
  final _controller = TextEditingController();
  double _price = 0;

  @override
  void initState() {
    super.initState();
    _controller.addListener(_onInputChanged);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _onInputChanged() {
    final value = double.tryParse(_controller.text.replaceAll(',', '.'));
    setState(() => _price = value ?? 0);
  }

  void _setAmount(double amount) {
    _controller.text = amount.toStringAsFixed(amount % 1 == 0 ? 0 : 2);
    _controller.selection = TextSelection.fromPosition(
      TextPosition(offset: _controller.text.length),
    );
  }

  @override
  Widget build(BuildContext context) {
    final profile = ref.watch(salaryProvider);
    final result = _price > 0
        ? Calculators.calculateWorkTime(
            purchasePrice: _price,
            profile: profile,
          )
        : null;
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final currency = profile.currency;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Werktijd Calculator'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Prijs',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 16),
                  TextField(
                    controller: _controller,
                    keyboardType: const TextInputType.numberWithOptions(
                      decimal: true,
                    ),
                    inputFormatters: [
                      FilteringTextInputFormatter.allow(
                        RegExp(r'^\d*[,\.]?\d{0,2}'),
                      ),
                    ],
                    decoration: InputDecoration(
                      prefixText: '${currency.symbol} ',
                      border: const OutlineInputBorder(),
                      hintText: '0,00',
                    ),
                    style: theme.textTheme.headlineSmall,
                    autofocus: true,
                  ),
                  const SizedBox(height: 16),
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: [
                      5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000,
                    ].map((amount) {
                      return ActionChip(
                        label: Text(
                          CurrencyFormatter.formatCompact(amount.toDouble(), currency),
                        ),
                        onPressed: () => _setAmount(amount.toDouble()),
                      );
                    }).toList(),
                  ),
                ],
              ),
            ),
          ),
          if (result != null) ...[
            const SizedBox(height: 16),
            Card(
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
                    const SizedBox(height: 20),
                    _ResultRow(
                      icon: Icons.timer_outlined,
                      label: 'Netto werktijd',
                      value: result.humanReadable,
                      color: colorScheme.primary,
                    ),
                    const SizedBox(height: 16),
                    _ResultRow(
                      icon: Icons.timer_off_outlined,
                      label: 'Bruto werktijd',
                      value: result.humanReadableGross,
                      color: colorScheme.secondary,
                    ),
                    const SizedBox(height: 16),
                    _ResultRow(
                      icon: Icons.repeat,
                      label: 'Dit is ${result.hours.toStringAsFixed(1)}x je uurloon',
                      value: CurrencyFormatter.formatHourly(
                        profile.hourlyWage,
                        currency,
                      ),
                      color: colorScheme.tertiary,
                    ),
                    const SizedBox(height: 16),
                    _ResultRow(
                      icon: Icons.calendar_today,
                      label: 'Je moet ${(result.hours / (profile.hoursPerWeek / 5)).toStringAsFixed(1)} dagen werken',
                      value: '${profile.hoursPerWeek.round()}u/week',
                      color: colorScheme.error,
                    ),
                    const SizedBox(height: 24),
                    _ProportionBar(
                      hours: result.hours,
                      hoursPerDay: profile.hoursPerWeek / 5,
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            _FunComparisons(
              price: _price,
              result: result,
              profile: profile,
            ),
          ],
        ],
      ),
    );
  }
}

class _ResultRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color color;

  const _ResultRow({
    required this.icon,
    required this.label,
    required this.value,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: color.withValues(alpha: 0.15),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Icon(icon, color: color, size: 20),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
              ),
              Text(
                value,
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class _ProportionBar extends StatelessWidget {
  final double hours;
  final double hoursPerDay;

  const _ProportionBar({
    required this.hours,
    required this.hoursPerDay,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final proportion = (hours / 40).clamp(0.0, 1.0);
    final days = (hours / hoursPerDay).toStringAsFixed(1);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Verhouding tot werkweek',
              style: theme.textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            Text(
              '$days werkdagen',
              style: theme.textTheme.bodySmall?.copyWith(
                fontWeight: FontWeight.w600,
                color: colorScheme.primary,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: LinearProgressIndicator(
            value: proportion,
            minHeight: 8,
            backgroundColor: colorScheme.surfaceContainerHighest,
            valueColor: AlwaysStoppedAnimation<Color>(
              proportion < 0.25
                  ? Colors.green
                  : proportion < 0.5
                      ? Colors.orange
                      : colorScheme.error,
            ),
          ),
        ),
      ],
    );
  }
}

class _FunComparisons extends StatelessWidget {
  final double price;
  final WorkTimeResult result;
  final SalaryProfile profile;

  const _FunComparisons({
    required this.price,
    required this.result,
    required this.profile,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final currency = profile.currency;

    final coffeePrice = 3.50;
    final coffeeCount = (price / coffeePrice).floor();

    final lunchPrice = 12.0;
    final lunchCount = (price / lunchPrice).floor();

    final moviePrice = 12.50;
    final movieCount = (price / moviePrice).floor();

    final comparisons = <_ComparisonItem>[];

    if (coffeeCount > 0) {
      comparisons.add(_ComparisonItem(
        emoji: '☕',
        text: '$coffeeCount kopjes koffie',
      ));
    }
    if (lunchCount > 0) {
      comparisons.add(_ComparisonItem(
        emoji: '🥪',
        text: '$lunchCount lunches',
      ));
    }
    if (movieCount > 0) {
      comparisons.add(_ComparisonItem(
        emoji: '🎬',
        text: '$movieCount bioscoopbezoeken',
      ));
    }

    if (comparisons.isEmpty) return const SizedBox.shrink();

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Dat is vergelijkbaar met',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            ...comparisons.map((item) {
              return Padding(
                padding: const EdgeInsets.only(bottom: 12),
                child: Row(
                  children: [
                    Text(
                      item.emoji,
                      style: const TextStyle(fontSize: 24),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        item.text,
                        style: theme.textTheme.bodyLarge,
                      ),
                    ),
                  ],
                ),
              );
            }),
          ],
        ),
      ),
    );
  }
}

class _ComparisonItem {
  final String emoji;
  final String text;

  const _ComparisonItem({
    required this.emoji,
    required this.text,
  });
}