import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/salary_profile.dart';
import '../models/tax_config.dart';
import '../models/currency.dart';
import '../providers/salary_provider.dart';
import '../utils/currency_formatter.dart';

class SalaryInputScreen extends ConsumerStatefulWidget {
  const SalaryInputScreen({super.key});

  @override
  ConsumerState<SalaryInputScreen> createState() => _SalaryInputScreenState();
}

class _SalaryInputScreenState extends ConsumerState<SalaryInputScreen> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _wageController;
  late TextEditingController _hoursController;
  late TextEditingController _holidayController;
  late TextEditingController _bonusController;
  late TextEditingController _overtimeController;
  late TextEditingController _weekendController;
  late TextEditingController _nightController;
  late TextEditingController _shiftController;
  late TaxConfig _selectedTaxConfig;
  late Currency _selectedCurrency;
  bool _initialized = false;

  @override
  void initState() {
    super.initState();
    _wageController = TextEditingController();
    _hoursController = TextEditingController();
    _holidayController = TextEditingController();
    _bonusController = TextEditingController();
    _overtimeController = TextEditingController();
    _weekendController = TextEditingController();
    _nightController = TextEditingController();
    _shiftController = TextEditingController();
    _selectedTaxConfig = TaxConfig.netherlands;
    _selectedCurrency = Currency.eur;
  }

  @override
  void dispose() {
    _wageController.dispose();
    _hoursController.dispose();
    _holidayController.dispose();
    _bonusController.dispose();
    _overtimeController.dispose();
    _weekendController.dispose();
    _nightController.dispose();
    _shiftController.dispose();
    super.dispose();
  }

  void _initControllers(SalaryProfile profile) {
    if (_initialized) return;
    _wageController.text = profile.hourlyWage.toString();
    _hoursController.text = profile.hoursPerWeek.toString();
    _holidayController.text = profile.holidayAllowancePercent.toString();
    _bonusController.text = profile.endOfYearBonusPercent.toString();
    _overtimeController.text = profile.overtimePercent.toString();
    _weekendController.text = profile.weekendPercent.toString();
    _nightController.text = profile.nightPercent.toString();
    _shiftController.text = profile.shiftPercent.toString();
    _selectedTaxConfig = profile.taxConfig;
    _selectedCurrency = profile.currency;
    _initialized = true;
  }

  double _parseDouble(String text) => double.tryParse(text) ?? 0;

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    final profile = ref.read(salaryProvider);
    final notifier = ref.read(salaryProvider.notifier);

    await Future.wait([
      notifier.updateWage(_parseDouble(_wageController.text)),
      notifier.updateHoursPerWeek(_parseDouble(_hoursController.text)),
      notifier.updateHolidayAllowance(_parseDouble(_holidayController.text)),
      notifier.updateEndOfYearBonus(_parseDouble(_bonusController.text)),
      notifier.updateOvertimePercent(_parseDouble(_overtimeController.text)),
      notifier.updateWeekendPercent(_parseDouble(_weekendController.text)),
      notifier.updateNightPercent(_parseDouble(_nightController.text)),
      notifier.updateShiftPercent(_parseDouble(_shiftController.text)),
      notifier.updateTaxConfig(_selectedTaxConfig),
      notifier.updateCurrency(_selectedCurrency),
    ]);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Salaris opgeslagen')),
      );
    }
  }

  SalaryProfile _buildPreviewProfile(SalaryProfile current) {
    return current.copyWith(
      hourlyWage: _parseDouble(_wageController.text),
      hoursPerWeek: _parseDouble(_hoursController.text),
      holidayAllowancePercent: _parseDouble(_holidayController.text),
      endOfYearBonusPercent: _parseDouble(_bonusController.text),
      overtimePercent: _parseDouble(_overtimeController.text),
      weekendPercent: _parseDouble(_weekendController.text),
      nightPercent: _parseDouble(_nightController.text),
      shiftPercent: _parseDouble(_shiftController.text),
      taxConfig: _selectedTaxConfig,
      currency: _selectedCurrency,
    );
  }

  @override
  Widget build(BuildContext context) {
    final profile = ref.watch(salaryProvider);
    _initControllers(profile);
    final theme = Theme.of(context);
    final preview = _buildPreviewProfile(profile);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Salaris gegevens'),
        centerTitle: true,
      ),
      body: GestureDetector(
        onTap: () => FocusScope.of(context).unfocus(),
        child: Form(
          key: _formKey,
          child: ListView(
            padding: const EdgeInsets.all(16),
            children: [
              _buildPreviewCard(theme, preview),
              const SizedBox(height: 16),
              _buildSectionTitle(theme, 'Basis gegevens'),
              const SizedBox(height: 8),
              _buildField(
                controller: _wageController,
                label: 'Uurloon',
                prefixText: '${_selectedCurrency.symbol} ',
                validator: (v) {
                  if (v == null || v.isEmpty) return 'Verplicht';
                  if (double.tryParse(v) == null) return 'Ongeldig getal';
                  return null;
                },
              ),
              const SizedBox(height: 12),
              _buildField(
                controller: _hoursController,
                label: 'Uren per week',
              ),
              const SizedBox(height: 24),
              _buildSectionTitle(theme, 'Toeslagen (%)'),
              const SizedBox(height: 8),
              _buildField(
                controller: _holidayController,
                label: 'Vakantiegeld',
                suffixText: '%',
              ),
              const SizedBox(height: 12),
              _buildField(
                controller: _bonusController,
                label: 'Eindejaarsbonus',
                suffixText: '%',
              ),
              const SizedBox(height: 12),
              _buildField(
                controller: _overtimeController,
                label: 'Overwerktoeslag',
                suffixText: '%',
              ),
              const SizedBox(height: 12),
              _buildField(
                controller: _weekendController,
                label: 'Weekendtoeslag',
                suffixText: '%',
              ),
              const SizedBox(height: 12),
              _buildField(
                controller: _nightController,
                label: 'Nachttoeslag',
                suffixText: '%',
              ),
              const SizedBox(height: 12),
              _buildField(
                controller: _shiftController,
                label: 'Ploegentoeslag',
                suffixText: '%',
              ),
              const SizedBox(height: 24),
              _buildSectionTitle(theme, 'Instellingen'),
              const SizedBox(height: 8),
              DropdownButtonFormField<TaxConfig>(
                value: _selectedTaxConfig,
                decoration: const InputDecoration(
                  labelText: 'Belastingregio',
                  border: OutlineInputBorder(),
                ),
                items: TaxConfig.all
                    .map((tc) => DropdownMenuItem(
                          value: tc,
                          child: Text(tc.name),
                        ))
                    .toList(),
                onChanged: (value) {
                  if (value != null) {
                    setState(() => _selectedTaxConfig = value);
                  }
                },
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<Currency>(
                value: _selectedCurrency,
                decoration: const InputDecoration(
                  labelText: 'Valuta',
                  border: OutlineInputBorder(),
                ),
                items: Currency.all
                    .map((c) => DropdownMenuItem(
                          value: c,
                          child: Text('${c.code} (${c.symbol})'),
                        ))
                    .toList(),
                onChanged: (value) {
                  if (value != null) {
                    setState(() => _selectedCurrency = value);
                  }
                },
              ),
              const SizedBox(height: 24),
              FilledButton.icon(
                onPressed: _save,
                icon: const Icon(Icons.save),
                label: const Text('Opslaan'),
              ),
              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPreviewCard(ThemeData theme, SalaryProfile preview) {
    final currency = _selectedCurrency;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Preview',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _PreviewItem(
                    label: 'Maandelijks bruto',
                    value: CurrencyFormatter.format(preview.monthlyGross, currency),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _PreviewItem(
                    label: 'Maandelijks netto',
                    value: CurrencyFormatter.format(preview.netMonthly, currency),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _PreviewItem(
                    label: 'Jaarlijks bruto',
                    value: CurrencyFormatter.format(preview.totalAnnualGross, currency),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _PreviewItem(
                    label: 'Belastingdruk',
                    value: '${(preview.effectiveTaxRate * 100).toStringAsFixed(1)}%',
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionTitle(ThemeData theme, String title) {
    return Text(
      title,
      style: theme.textTheme.titleSmall?.copyWith(
        color: theme.colorScheme.primary,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Widget _buildField({
    required TextEditingController controller,
    required String label,
    String? prefixText,
    String? suffixText,
    String? Function(String?)? validator,
  }) {
    return TextFormField(
      controller: controller,
      keyboardType: const TextInputType.numberWithOptions(decimal: true),
      decoration: InputDecoration(
        labelText: label,
        prefixText: prefixText,
        suffixText: suffixText,
        border: const OutlineInputBorder(),
        isDense: true,
      ),
      validator: validator,
      onChanged: (_) => setState(() {}),
    );
  }
}

class _PreviewItem extends StatelessWidget {
  final String label;
  final String value;

  const _PreviewItem({required this.label, required this.value});

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
        const SizedBox(height: 2),
        Text(
          value,
          style: theme.textTheme.bodyLarge?.copyWith(
            fontWeight: FontWeight.w600,
          ),
        ),
      ],
    );
  }
}
