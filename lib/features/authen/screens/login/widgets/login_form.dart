import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../../utils/constants/colors.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../../utils/constants/texts_strings.dart';
import '../../../../../utils/helpers/helper_functions.dart';
import '../../../../../utils/validators/validation.dart';
import '../../../controllers/login/login_controller.dart';

class ELoginForm extends StatelessWidget {
  const ELoginForm({
    super.key,
  });

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
            // Email Field
            TextFormField(
              validator: (value) => EValidation.validateEmail(value),
              controller: controller.email,
              decoration: InputDecoration(
                prefixIcon: const Icon(Iconsax.direct_right),
                labelStyle: const TextStyle(color: Colors.grey),
                label: Text(
                  ETexts.email,
                  style: TextStyle(
                      color: dark ? EColors.thirdColor : EColors.primaryColor),
                ),
              ),
            ),
            const SizedBox(
              height: ESizes.inputBetweenFields,
            ),

            // Password Field
            Obx(
                  () => TextFormField(
                validator: (value) =>
                    EValidation.validateEmptyText('Password', value),
                controller: controller.password,
                obscureText: controller.hidePassword.value,
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
                    onPressed: () => controller.hidePassword.value =
                    !controller.hidePassword.value,
                    icon: Icon(controller.hidePassword.value
                        ? Iconsax.eye_slash
                        : Iconsax.eye),
                  ),
                ),
              ),
            ),
            const SizedBox(height: ESizes.inputBetweenFields / 2),

            // Remember Me & Forget Password
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                // Remember Me Checkbox
                Row(
                  children: [
                    SizedBox(
                      width: 24,
                      height: 24,
                      child: Obx(
                            () => Checkbox(
                          value: controller.remember.value,
                          onChanged: (value) => controller.remember.value =
                          !controller.remember.value,
                        ),
                      ),
                    ),
                    const Text(ETexts.rememberMe),
                  ],
                ),

                // Forget Password Button
                TextButton(
                  onPressed: () => Get.toNamed('/forget-password'),
                  child: const Text(
                    ETexts.forgetPassword,
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: ESizes.defaultBetweenSections,
            ),

            // Sign In Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () => controller.emailAndPasswordSignIn(),
                child: const Text(ETexts.signIn),
              ),
            ),
            const SizedBox(
              height: ESizes.defaultBetweenItem,
            ),

            // Create Account Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () => Get.toNamed('/signup'),
                child: const Text(ETexts.createAccount),
              ),
            ),
          ],
        ),
      ),
    );
  }
}