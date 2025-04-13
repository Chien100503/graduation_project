import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/authen/screens/onboarding/onboarding.dart';
import 'package:pet_shop/routes/app_routes.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/theme/theme.dart';

import 'bindings/general_bindings.dart';

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      themeMode: ThemeMode.system,
      theme: EAppTheme.lightTheme,
      darkTheme: EAppTheme.darkTheme,
      // initialBinding: GeneralBindings(),
      // getPages: AppRoutes.pages,
      // home: const Scaffold(backgroundColor: EColors.thirdColor, body: Center(child: CircularProgressIndicator(color: EColors.thirdColor,),),),
      home: const OnboardingScreen(),
    );
  }
}
