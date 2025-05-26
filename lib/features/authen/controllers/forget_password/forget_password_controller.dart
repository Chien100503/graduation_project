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
      await _userRepository.forgotPassword(email.value); // Nếu lỗi, sẽ vào catch
      errorMessage.value = '';
      ECustomSnackBar.showSuccess(
        title: 'Thành công',
        message: 'Mã khôi phục đã được gửi đến email của bạn.',
      );
      Get.offAll(const LoginScreen());
    } catch (e) {
      errorMessage.value = e.toString().replaceAll('Exception: ', '');
      ECustomSnackBar.showError(
        title: 'Lỗi',
        message: errorMessage.value,
      );
    } finally {
      isLoading.value = false;
    }
  }


}
