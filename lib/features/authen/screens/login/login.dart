import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/authen/screens/login/widgets/login_divider.dart';
import 'package:pet_shop/features/authen/screens/login/widgets/login_form.dart';
import 'package:pet_shop/features/authen/screens/login/widgets/login_header.dart';
import 'package:pet_shop/features/authen/screens/login/widgets/login_social_button.dart';

import '../../../../common/styles/spacing_styles.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/constants/texts_strings.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        padding: ESpacingStyle.paddingWithAppBarHeight,
        child: Column(
          children: [
            // logo, title & sub-title
            const ELoginHeader(),
            // Form
            const ELoginForm(),

            const SizedBox(
              height: ESizes.defaultBetweenItem,
            ),
            // divider
            EFormDivider(dividerText: ETexts.orSignInWith.capitalize!,),
            const SizedBox(
              height: ESizes.defaultBetweenSections,
            ),
            //footer
            const ESocialButtons()
          ],
        ),
      ),
    );
  }
}








