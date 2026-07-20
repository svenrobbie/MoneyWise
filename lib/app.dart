import 'package:flutter/material.dart';
import 'package:dynamic_color/dynamic_color.dart';
import 'screens/home_screen.dart';

class MoneyWiseApp extends StatelessWidget {
  const MoneyWiseApp({super.key});

  @override
  Widget build(BuildContext context) {
    return DynamicColorBuilder(
      builder: (ColorScheme? lightDynamic, ColorScheme? darkDynamic) {
        final lightColorScheme = lightDynamic?.copyWith(
          primary: Colors.teal,
        ) ?? ColorScheme.fromSeed(seedColor: Colors.teal);

        final darkColorScheme = darkDynamic?.copyWith(
          primary: Colors.tealAccent,
        ) ?? ColorScheme.fromSeed(
          seedColor: Colors.tealAccent,
          brightness: Brightness.dark,
        );

        return MaterialApp(
          title: 'MoneyWise',
          debugShowCheckedModeBanner: false,
          theme: ThemeData(
            colorScheme: lightColorScheme,
            useMaterial3: true,
            fontFamily: 'Roboto',
            cardTheme: CardTheme(
              elevation: 0,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(
                  color: lightColorScheme.outlineVariant.withValues(alpha: 0.3),
                ),
              ),
            ),
          ),
          darkTheme: ThemeData(
            colorScheme: darkColorScheme,
            useMaterial3: true,
            fontFamily: 'Roboto',
            cardTheme: CardTheme(
              elevation: 0,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(
                  color: darkColorScheme.outlineVariant.withValues(alpha: 0.3),
                ),
              ),
            ),
          ),
          themeMode: ThemeMode.system,
          home: const HomeScreen(),
        );
      },
    );
  }
}
