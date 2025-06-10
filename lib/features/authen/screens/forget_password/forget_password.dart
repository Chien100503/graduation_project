import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/texts_strings.dart';
import '../../../../utils/validators/validation.dart';
import '../../controllers/forget_password/forget_password_controller.dart';

class ForgotPasswordScreen extends StatelessWidget {
  const ForgotPasswordScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final formKey = GlobalKey<FormState>();
    final dark = EHelperFunctions.isDarkMode(context);
    final ForgotPasswordController controller = Get.put(ForgotPasswordController());

    return Scaffold(
      appBar: AppBar(
        title: Text('Quên mật khẩu', style: Theme.of(context).textTheme.headlineMedium,),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Obx(() {
          return Form(
            key: formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Enter your email to receive your password recovery code.',
                  style: TextStyle(fontSize: 16),
                ),
                const SizedBox(height: 20),
                TextFormField(
                  validator: (value) => EValidation.validateEmail(value),
                  onChanged: (value) {
                    controller.email.value = value;
                  },
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
                const SizedBox(height: 20),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: controller.isLoading.value
                        ? null
                        : () async {
                      if (formKey.currentState!.validate()) {
                        await controller.requestPasswordReset();
                      }
                    },
                    child: controller.isLoading.value
                        ? const CircularProgressIndicator(color: Colors.white)
                        : const Text('Reset'),
                  ),
                ),
              ],
            ),
          );
        }),
      ),
    );
  }
}
