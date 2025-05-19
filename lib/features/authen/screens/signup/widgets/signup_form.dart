import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../../utils/constants/colors.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../../utils/constants/texts_strings.dart';
import '../../../../../utils/helpers/helper_functions.dart';
import '../../../../../utils/validators/validation.dart';
import '../../../controllers/signup/signup_controller.dart';

class SignupForm extends StatelessWidget {
  const SignupForm({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    final controller = Get.put(SignupController());

    return Form(
      key: controller.signupFormKey,
      child: Column(
        children: [
          Row(
            children: [
              // First Name
              Expanded(
                child: TextFormField(
                  controller: controller.firstName,
                  validator: (value) =>
                      EValidation.validateEmptyText('First name', value),
                  decoration: InputDecoration(
                    label: Text(
                      ETexts.firstName,
                      style: TextStyle(
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                    ),
                    prefixIcon: const Icon(Iconsax.user),
                  ),
                ),
              ),
              const SizedBox(width: ESizes.inputBetweenFields),
              // Last Name
              Expanded(
                child: TextFormField(
                  controller: controller.lastName,
                  validator: (value) =>
                      EValidation.validateEmptyText('Last name', value),
                  decoration: InputDecoration(
                    label: Text(
                      ETexts.lastName,
                      style: TextStyle(
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                    ),
                    prefixIcon: const Icon(Iconsax.user),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: ESizes.inputBetweenFields),

          // Username
          TextFormField(
            controller: controller.name,
            validator: (value) =>
                EValidation.validateEmptyText('Username', value),
            decoration: InputDecoration(
              label: Text(
                ETexts.userName,
                style: TextStyle(
                    color: dark ? EColors.thirdColor : EColors.primaryColor),
              ),
              prefixIcon: const Icon(Iconsax.user_edit),
            ),
          ),
          const SizedBox(height: ESizes.inputBetweenFields),

          // Email
          TextFormField(
            controller: controller.email,
            validator: (value) => EValidation.validateEmail(value),
            decoration: InputDecoration(
              label: Text(
                ETexts.email,
                style: TextStyle(
                    color: dark ? EColors.thirdColor : EColors.primaryColor),
              ),
              prefixIcon: const Icon(Iconsax.direct),
            ),
          ),
          const SizedBox(height: ESizes.inputBetweenFields),

          // Phone Number
          TextFormField(
            controller: controller.phoneNumber,
            validator: (value) => EValidation.validatePhoneNumber(value),
            decoration: InputDecoration(
              label: Text(
                ETexts.phoneNumber,
                style: TextStyle(
                    color: dark ? EColors.thirdColor : EColors.primaryColor),
              ),
              prefixIcon: const Icon(Iconsax.call),
            ),
          ),
          const SizedBox(height: ESizes.inputBetweenFields),

          // Password
          Obx(
                () => TextFormField(
              controller: controller.password,
              obscureText: controller.hidePassword.value,
              validator: (value) => EValidation.validatePassword(value),
              decoration: InputDecoration(
                label: Text(
                  ETexts.password,
                  style: TextStyle(
                      color: dark ? EColors.thirdColor : EColors.primaryColor),
                ),
                prefixIcon: const Icon(Iconsax.password_check),
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
          const SizedBox(height: ESizes.inputBetweenFields),

          // Confirm Password
          Obx(
                () => TextFormField(
              controller: controller.confirmPassword,
              obscureText: controller.hideConfirmPassword.value,
              validator: (value) => EValidation.validateConfirmPassword(
                controller.password.text,
                value,
              ),
              decoration: InputDecoration(
                label: Text(
                  'Confirm password',
                  style: TextStyle(
                      color: dark ? EColors.thirdColor : EColors.primaryColor),
                ),
                prefixIcon: const Icon(Iconsax.password_check),
                suffixIcon: IconButton(
                  onPressed: () => controller.hideConfirmPassword.value =
                  !controller.hideConfirmPassword.value,
                  icon: Icon(controller.hideConfirmPassword.value
                      ? Iconsax.eye_slash
                      : Iconsax.eye),
                ),
              ),
            ),
          ),
          const SizedBox(height: ESizes.defaultBetweenSections),

          // Create Account Button
          SizedBox(
            width: double.infinity,
            child: Obx(
                  () => ElevatedButton(
                onPressed: controller.isLoading.value
                    ? null
                    : () => controller.signup(),
                child: controller.isLoading.value
                    ? const CircularProgressIndicator()
                    : const Text(ETexts.createAccount),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
