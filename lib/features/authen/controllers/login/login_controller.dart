import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class LoginController extends GetxController {
  static LoginController get instance => Get.find();

  // Variables for UI state management
  final remember = false.obs;
  final hidePassword = true.obs;
  final localStorage = GetStorage();
  final email = TextEditingController();
  final password = TextEditingController();

  GlobalKey<FormState> loginFormKey = GlobalKey<FormState>();

  @override
  void onInit() {
    // Load saved credentials if any
    email.text = localStorage.read('REMEMBER_EMAIL') ?? '';
    password.text = localStorage.read('REMEMBER_PASSWORD') ?? '';
    super.onInit();
  }

  // Mock login function for UI demo only
  Future<void> emailAndPasswordSignIn() async {
    try {
      // Show a mock loading dialog
      showLoadingDialog('Logging you in...');

      // Validate the form
      if (!loginFormKey.currentState!.validate()) {
        hideLoadingDialog();
        return;
      }

      // Save data if remember is checked
      if (remember.value) {
        localStorage.write('REMEMBER_EMAIL', email.text.trim());
        localStorage.write('REMEMBER_PASSWORD', password.text.trim());
      }

      // Simulate network delay
      await Future.delayed(const Duration(seconds: 2));

      // Hide loading dialog
      hideLoadingDialog();

      // Show success message
      showSuccessSnackBar('Login Successful', 'Welcome to the app');

      // Navigate to home or dashboard (mock navigation)
      Get.offAllNamed('/home');

    } catch (e) {
      hideLoadingDialog();
      showErrorSnackBar('Oh Snap', 'Something went wrong');
    }
  }

  // Mock Google Sign In function for UI demo only
  Future<void> googleSignIn() async {
    try {
      // Show loading dialog
      showLoadingDialog('Logging you in with Google...');

      // Simulate network delay
      await Future.delayed(const Duration(seconds: 2));

      // Hide loading dialog
      hideLoadingDialog();

      // Show success message
      showSuccessSnackBar('Login Successful', 'Welcome to the app');

      // Navigate to home or dashboard (mock navigation)
      Get.offAllNamed('/home');

    } catch (e) {
      hideLoadingDialog();
      showErrorSnackBar('Oh Snap', 'Something went wrong');
    }
  }

  // Helper methods for UI feedback
  void showLoadingDialog(String message) {
    Get.dialog(
      Dialog(
        backgroundColor: Colors.transparent,
        elevation: 0,
        child: Center(
          child: Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const CircularProgressIndicator(),
                const SizedBox(height: 20),
                Text(message),
              ],
            ),
          ),
        ),
      ),
      barrierDismissible: false,
    );
  }

  void hideLoadingDialog() {
    if (Get.isDialogOpen ?? false) {
      Get.back();
    }
  }

  void showSuccessSnackBar(String title, String message) {
    Get.snackbar(
      title,
      message,
      backgroundColor: Colors.green.withOpacity(0.7),
      colorText: Colors.white,
      snackPosition: SnackPosition.BOTTOM,
      margin: const EdgeInsets.all(10),
    );
  }

  void showErrorSnackBar(String title, String message) {
    Get.snackbar(
      title,
      message,
      backgroundColor: Colors.red.withOpacity(0.7),
      colorText: Colors.white,
      snackPosition: SnackPosition.BOTTOM,
      margin: const EdgeInsets.all(10),
    );
  }
}