import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/authen/screens/signup/widgets/signup_form.dart';
import 'package:pet_shop/features/authen/screens/signup/widgets/term_conditions_checkbox.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/constants/texts_strings.dart';
import '../login/widgets/login_divider.dart';
import '../login/widgets/login_social_button.dart';


class SignupScreen extends StatelessWidget {
  const SignupScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const EAppBar(showBackArrow: true),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(ESizes.defaultSpace),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // title
              Text(ETexts.signupTitle,
                  style: Theme.of(context).textTheme.headlineMedium),

              const SizedBox(height: ESizes.defaultBetweenSections),

              // Form
              const SignupForm(),
              // term & conditions Checkbox
              const SizedBox(height: ESizes.defaultBetweenSections),
              const ETermConditionsCheckbox(),

              const SizedBox(height: ESizes.defaultBetweenItem),
              // Divider
              EFormDivider(dividerText: ETexts.orSignUpWith.capitalize!),
              const SizedBox(
                height: ESizes.defaultBetweenItem,
              ),
              const ESocialButtons(),
            ],
          ),
        ),
      ),
    );
  }
}
