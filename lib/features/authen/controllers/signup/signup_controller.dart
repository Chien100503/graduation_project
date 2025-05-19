import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../../common/widgets/loader/loader.dart';
import '../../../../data/repositories/user_repository.dart';
import '../../../../navigation_menu.dart';
import '../../../personalizations/models/register_model.dart';
import '../../screens/verify/VerifyPinScreen.dart';

class SignupController extends GetxController {
  static SignupController get instance => Get.find();

  final UserRepository _userRepository = UserRepository();

  final email = TextEditingController();
  final password = TextEditingController();
  final confirmPassword = TextEditingController();
  final firstName = TextEditingController();
  final lastName = TextEditingController();
  final phoneNumber = TextEditingController();
  final name = TextEditingController();

  final hidePassword = true.obs;
  final RxBool hideConfirmPassword = true.obs;
  final privacyPolicy = true.obs;
  final isLoading = false.obs;
  final signupFormKey = GlobalKey<FormState>();

  /// Đăng ký
  Future<void> signup() async {
    if (!signupFormKey.currentState!.validate()) return;

    try {
      isLoading.value = true;

      if (!privacyPolicy.value) {
        ECustomSnackBar.showWarning(
          title: 'Chấp nhận điều khoản',
          message: 'Bạn phải chấp nhận chính sách bảo mật để tiếp tục.',
        );
        isLoading.value = false;
        return;
      }

      final model = RegisterModel(
        email: email.text.trim(),
        name: name.text.trim(),
        password: password.text.trim(),
        confirmPassword: confirmPassword.text.trim(),
        firstName: firstName.text.trim(),
        lastName: lastName.text.trim(),
        phone: phoneNumber.text.trim(),
      );

      await _userRepository.registerUser(model);

      isLoading.value = false;
      Get.to(() => VerifyPinScreen(email: model.email));
    } catch (e) {
      isLoading.value = false;
      Get.snackbar('Lỗi đăng ký', e.toString(),
          backgroundColor: Colors.red, colorText: Colors.white);
    }
  }

  /// Xác minh mã PIN
  Future<void> verifyPin(String code) async {
    try {
      isLoading.value = true;
      final isActive = await _userRepository.verifyPinCode(code);
      isLoading.value = false;

      if (isActive) {
        Get.offAll(() => const NavigationMenu());
      } else {
        Get.snackbar('Xác minh thất bại', 'Tài khoản chưa được kích hoạt.',
            backgroundColor: Colors.orange);
      }
    } catch (e) {
      isLoading.value = false;
      Get.snackbar('Lỗi xác minh', e.toString(),
          backgroundColor: Colors.red, colorText: Colors.white);
      print('err: ${e.toString()}');
    }
  }

  // RESEND PINCODE
  Future<void> resendCode() async {
    try {
      await _userRepository.resendCode();
      Get.snackbar('Thành công', 'Mã PIN đã được gửi lại.');
    } catch (e) {
      Get.snackbar('Lỗi', e.toString());
    }
  }
}