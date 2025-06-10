import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import '../../../../data/repositories/user_repository.dart';
import '../../../../navigation_menu.dart';

class LoginController extends GetxController {
  static LoginController get instance => Get.find();

  // UI state
  final remember = false.obs;
  final hidePassword = true.obs;
  final isLoading = false.obs;

  // Form & controllers
  final localStorage = GetStorage();
  final email = TextEditingController();
  final password = TextEditingController();
  final loginFormKey = GlobalKey<FormState>();

  // Repository
  final _userRepo = UserRepository();


  @override
  void onInit() {
    email.text = localStorage.read('REMEMBER_EMAIL') ?? '';
    password.text = localStorage.read('REMEMBER_PASSWORD') ?? '';
    super.onInit();
  }

  Future<void> emailAndPasswordSignIn() async {
    if (!loginFormKey.currentState!.validate()) return;

    try {
      isLoading.value = true;
      final result = await _userRepo.loginUser(
        email.text.trim(),
        password.text.trim(),
      );

      final token = result['token'];
      localStorage.write('JWT_TOKEN', token);

      if (remember.value) {
        localStorage.write('REMEMBER_EMAIL', email.text.trim());
        localStorage.write('REMEMBER_PASSWORD', password.text.trim());
      } else {
        localStorage.remove('REMEMBER_EMAIL');
        localStorage.remove('REMEMBER_PASSWORD');
      }

      // Lấy thông tin đơn giản từ response
      final userEmail = email.text.trim();

      showSuccessSnackBar('Đăng nhập thành công', 'Chào mừng $userEmail');
      Get.to(() => const NavigationMenu());

    } catch (e) {
      showErrorSnackBar('Đăng nhập thất bại', e.toString().replaceAll('Exception:', '').trim());
      print('Error during login: ${e.toString()}');
      print('Email: ${email.text.trim()}');
      print('Password: ${password.text.trim()}');
    } finally {
      isLoading.value = false;
    }
  }

  // UI feedback
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
