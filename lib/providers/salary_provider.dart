import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/salary_profile.dart';
import '../models/tax_config.dart';
import '../models/currency.dart';

const _key = 'salary_profile';

final salaryProvider = NotifierProvider<SalaryNotifier, SalaryProfile>(
  SalaryNotifier.new,
);

class SalaryNotifier extends Notifier<SalaryProfile> {
  @override
  SalaryProfile build() {
    _load();
    return SalaryProfile.defaultProfile();
  }

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    final json = prefs.getString(_key);
    if (json != null) {
      try {
        final data = jsonDecode(json) as Map<String, dynamic>;
        state = SalaryProfile.fromJson(data);
      } catch (_) {}
    }
  }

  Future<void> _save() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_key, jsonEncode(state.toJson()));
  }

  Future<void> updateWage(double hourlyWage) async {
    state = state.copyWith(hourlyWage: hourlyWage);
    await _save();
  }

  Future<void> updateHoursPerWeek(double hours) async {
    state = state.copyWith(hoursPerWeek: hours);
    await _save();
  }

  Future<void> updateHolidayAllowance(double percent) async {
    state = state.copyWith(holidayAllowancePercent: percent);
    await _save();
  }

  Future<void> updateEndOfYearBonus(double percent) async {
    state = state.copyWith(endOfYearBonusPercent: percent);
    await _save();
  }

  Future<void> updateOvertimePercent(double percent) async {
    state = state.copyWith(overtimePercent: percent);
    await _save();
  }

  Future<void> updateWeekendPercent(double percent) async {
    state = state.copyWith(weekendPercent: percent);
    await _save();
  }

  Future<void> updateNightPercent(double percent) async {
    state = state.copyWith(nightPercent: percent);
    await _save();
  }

  Future<void> updateShiftPercent(double percent) async {
    state = state.copyWith(shiftPercent: percent);
    await _save();
  }

  Future<void> updateTaxConfig(TaxConfig config) async {
    state = state.copyWith(taxConfig: config);
    await _save();
  }

  Future<void> updateCurrency(Currency currency) async {
    state = state.copyWith(currency: currency);
    await _save();
  }

  Future<void> updateAll(SalaryProfile profile) async {
    state = profile;
    await _save();
  }
}
