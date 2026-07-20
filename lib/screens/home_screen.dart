import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../models/salary_profile.dart';
import '../providers/salary_provider.dart';
import '../utils/currency_formatter.dart';
import 'salary_input_screen.dart';
import 'work_time_screen.dart';
import 'savings_screen.dart';
import 'investment_screen.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profile = ref.watch(salaryProvider);
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final now = DateTime.now();
    final greeting = _greeting(now);

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar.large(
            title: const Text('MoneyWise'),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 8),
                  Text(
                    greeting,
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    DateFormat('EEEE d MMMM yyyy').format(now),
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                  const SizedBox(height: 24),
                  _SummaryCard(profile: profile),
                  const SizedBox(height: 24),
                  Text(
                    'Navigatie',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                  const SizedBox(height: 12),
                ],
              ),
            ),
          ),
          SliverPadding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            sliver: SliverGrid.count(
              crossAxisCount: 2,
              mainAxisSpacing: 12,
              crossAxisSpacing: 12,
              childAspectRatio: 1.1,
              children: [
                _NavCard(
                  icon: Icons.payments_outlined,
                  label: 'Salaris',
                  subtitle: 'Invoer',
                  color: colorScheme.primaryContainer,
                  foregroundColor: colorScheme.onPrimaryContainer,
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const SalaryInputScreen(),
                    ),
                  ),
                ),
                _NavCard(
                  icon: Icons.timer_outlined,
                  label: 'Werktijd',
                  subtitle: 'Calculator',
                  color: colorScheme.secondaryContainer,
                  foregroundColor: colorScheme.onSecondaryContainer,
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const WorkTimeScreen(),
                    ),
                  ),
                ),
                _NavCard(
                  icon: Icons.savings_outlined,
                  label: 'Sparen',
                  subtitle: 'Calculator',
                  color: colorScheme.tertiaryContainer,
                  foregroundColor: colorScheme.onTertiaryContainer,
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const SavingsScreen(),
                    ),
                  ),
                ),
                _NavCard(
                  icon: Icons.trending_up,
                  label: 'Beleggen',
                  subtitle: 'Calculator',
                  color: colorScheme.errorContainer,
                  foregroundColor: colorScheme.onErrorContainer,
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const InvestmentScreen(),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 32)),
        ],
      ),
    );
  }

  String _greeting(DateTime date) {
    final hour = date.hour;
    if (hour < 12) return 'Goedemorgen';
    if (hour < 18) return 'Goedemiddag';
    return 'Goedenavond';
  }
}

class _SummaryCard extends StatelessWidget {
  final SalaryProfile profile;

  const _SummaryCard({required this.profile});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final currency = profile.currency;

    return Card(
      clipBehavior: Clip.antiAlias,
      child: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              colorScheme.primaryContainer,
              colorScheme.primaryContainer.withValues(alpha: 0.4),
            ],
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Icon(
                    Icons.account_balance_wallet_outlined,
                    color: colorScheme.onPrimaryContainer,
                    size: 20,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'Salaris Overzicht',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: colorScheme.onPrimaryContainer,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              _SummaryRow(
                label: 'Uurloon',
                value: CurrencyFormatter.formatHourly(
                  profile.hourlyWage,
                  currency,
                ),
              ),
              const SizedBox(height: 8),
              _SummaryRow(
                label: 'Netto per maand',
                value: CurrencyFormatter.formatMonthly(
                  profile.netMonthly,
                  currency,
                ),
              ),
              const SizedBox(height: 8),
              _SummaryRow(
                label: 'Netto per jaar',
                value: CurrencyFormatter.formatAnnual(
                  profile.netAnnual,
                  currency,
                ),
              ),
              const Divider(height: 24),
              _SummaryRow(
                label: 'Bruto per jaar',
                value: CurrencyFormatter.formatCompact(
                  profile.totalAnnualGross,
                  currency,
                ),
                small: true,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _SummaryRow extends StatelessWidget {
  final String label;
  final String value;
  final bool small;

  const _SummaryRow({
    required this.label,
    required this.value,
    this.small = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: (small ? theme.textTheme.bodySmall : theme.textTheme.bodyLarge)
              ?.copyWith(
            color: colorScheme.onPrimaryContainer.withValues(alpha: 0.7),
          ),
        ),
        Text(
          value,
          style: (small ? theme.textTheme.bodySmall : theme.textTheme.titleMedium)
              ?.copyWith(
            fontWeight: FontWeight.w600,
            color: colorScheme.onPrimaryContainer,
          ),
        ),
      ],
    );
  }
}

class _NavCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final String subtitle;
  final Color color;
  final Color foregroundColor;
  final VoidCallback onTap;

  const _NavCard({
    required this.icon,
    required this.label,
    required this.subtitle,
    required this.color,
    required this.foregroundColor,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: color,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(icon, color: foregroundColor, size: 24),
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    label,
                    style: theme.textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
