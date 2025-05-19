import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/loader/loader.dart';
import 'package:pet_shop/data/repositories/user_repository.dart';
import 'package:pet_shop/features/authen/screens/login/login.dart';
class ForgotPasswordController extends GetxController {
  final UserRepository _userRepository = UserRepository();
  RxString email = ''.obs;
  RxBool isLoading = false.obs;
  RxString errorMessage = ''.obs;

  // Gọi API quên mật khẩu
  Future<void> requestPasswordReset() async {
    if (email.value.isEmpty) {
      errorMessage.value = 'Vui lòng nhập email.';
      return;
    }

    isLoading.value = true;
    try {
      await _userRepository.forgotPassword(email.value);
      errorMessage.value = '';  // Clear error message if successful
      // Hiển thị thông báo cho người dùng đã gửi mã reset
      ECustomSnackBar.showSuccess(title: 'Thành công' ,message: 'Mã khôi phục đã được gửi đến email của bạn.');
      // Get.snackbar('Thành công', 'Mã khôi phục đã được gửi đến email của bạn.',
      //     snackPosition: SnackPosition.BOTTOM, animationDuration: const Duration(seconds: 3));
      Get.offAll(const LoginScreen());
    } catch (e) {
      errorMessage.value = e.toString();
      ECustomSnackBar.showError(title: 'Error', message: e.toString());
    } finally {
      isLoading.value = false;
    }
  }
}
