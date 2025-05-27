import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/features/authen/screens/forget_password/forget_password.dart';
import 'package:pet_shop/features/authen/screens/signup/signup.dart';

import '../../../../../utils/constants/colors.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../../utils/constants/texts_strings.dart';
import '../../../../../utils/helpers/helper_functions.dart';
import '../../../../../utils/validators/validation.dart';
import '../../../controllers/login/login_controller.dart';

class ELoginForm extends StatelessWidget {
  const ELoginForm({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    final controller = Get.put(LoginController());

    return Form(
      key: controller.loginFormKey,
      child: Padding(
        padding:
            const EdgeInsets.symmetric(vertical: ESizes.defaultBetweenSections),
        child: Column(
          children: [
            /// Email Field
            TextFormField(
              controller: controller.email,
              validator: (value) => EValidation.validateEmail(value),
              decoration: InputDecoration(
                prefixIcon: const Icon(Iconsax.direct_right),
                labelStyle: const TextStyle(color: Colors.grey),
                label: Text(
                  ETexts.email,
                  style: TextStyle(
                    color: dark ? EColors.thirdColor : EColors.primaryColor,
                  ),
                ),
              ),
            ),
            const SizedBox(height: ESizes.inputBetweenFields),

            /// Password Field
            Obx(
              () => TextFormField(
                controller: controller.password,
                obscureText: controller.hidePassword.value,
                validator: (value) =>
                    EValidation.validateEmptyText('Password', value),
                decoration: InputDecoration(
                  prefixIcon: const Icon(Iconsax.password_check),
                  labelStyle: const TextStyle(color: Colors.grey),
                  label: Text(
                    ETexts.password,
                    style: TextStyle(
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                  ),
                  suffixIcon: IconButton(
                    icon: Icon(controller.hidePassword.value
                        ? Iconsax.eye_slash
                        : Iconsax.eye),
                    onPressed: () => controller.hidePassword.toggle(),
                  ),
                ),
              ),
            ),
            const SizedBox(height: ESizes.inputBetweenFields / 2),

            /// Remember Me & Forgot Password
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Obx(
                      () => Checkbox(
                        value: controller.remember.value,
                        onChanged: (value) =>
                            controller.remember.value = value ?? false,
                      ),
                    ),
                    const Text(ETexts.rememberMe),
                  ],
                ),
                TextButton(
                  onPressed: () => Get.to(() => ForgotPasswordScreen()),
                  child: const Text(ETexts.forgetPassword),
                ),
              ],
            ),
            const SizedBox(height: ESizes.defaultBetweenSections),

            /// Sign In Button
            Obx(() => SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: controller.isLoading.value
                        ? null
                        : controller.emailAndPasswordSignIn,
                    child: controller.isLoading.value
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              valueColor: AlwaysStoppedAnimation(Colors.white),
                            ),
                          )
                        : const Text(ETexts.signIn),
                  ),
                )),
            const SizedBox(height: ESizes.defaultBetweenItem),

            /// Create Account Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () => Get.to(
                  const SignupScreen(),
                ),
                child: const Text(
                  ETexts.createAccount,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
