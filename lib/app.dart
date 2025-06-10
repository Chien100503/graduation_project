import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/authen/screens/login/login.dart';
import 'package:pet_shop/routes/app_routes.dart';
import 'package:pet_shop/utils/theme/theme.dart';
import 'bindings/general_bindings.dart';
import 'features/authen/screens/onboarding/onboarding.dart';
import 'navigation_menu.dart';

class App extends StatelessWidget {
  final bool isFirstTime;
  final bool isLoggedIn;

  const App({super.key, required this.isFirstTime, required this.isLoggedIn});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      themeMode: ThemeMode.system,
      theme: EAppTheme.lightTheme,
      darkTheme: EAppTheme.darkTheme,
      initialBinding: GeneralBindings(),
      getPages: AppRoutes.pages,
      home: isFirstTime
          ? const OnboardingScreen()
          : isLoggedIn
          ? const NavigationMenu()
          : const LoginScreen(),
    );
  }
}

