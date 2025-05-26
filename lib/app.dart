import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/authen/screens/onboarding/onboarding.dart';
import 'package:pet_shop/utils/theme/theme.dart';

import 'bindings/general_bindings.dart';
import 'features/authen/screens/login/login.dart';


class App extends StatelessWidget {
  final bool isFirstTime;
  const App({super.key, required this.isFirstTime});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      themeMode: ThemeMode.system,
      theme: EAppTheme.lightTheme,
      darkTheme: EAppTheme.darkTheme,
      initialBinding: GeneralBindings(),
      // getPages: AppRoutes.pages,
      // home: const Scaffold(backgroundColor: EColors.thirdColor, body: Center(child: CircularProgressIndicator(color: EColors.thirdColor,),),),
      home: isFirstTime ? const OnboardingScreen() : const LoginScreen(),
    );
  }
}
