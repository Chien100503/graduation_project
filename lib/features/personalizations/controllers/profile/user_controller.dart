import 'dart:io';

import 'package:get/get.dart';
import '../../../../common/widgets/loader/loader.dart';
import '../../../../data/repositories/user_repository.dart';
import '../../models/user_model.dart';

class UserController extends GetxController {
  static UserController get instance => Get.find();

  final _userRepository = UserRepository();
  // Rx<UserModel> profile = UserModel().obs;
  var profile = UserModel.empty().obs;
  var isLoading = false.obs;

  @override
  void onInit() {
    fetchUserProfile();
    super.onInit();
  }


  Future<void> fetchUserProfile() async {
    try {
      isLoading.value = true;
      final result = await _userRepository.getProfile();
      profile.value = result;
    } catch (e) {
      print('Lỗi khi lấy profile: $e');
    } finally {
      isLoading.value = false;
    }
  }

  // ✅ Cập nhật profile và lấy lại dữ liệu mới từ server
  Future<void> updateUserProfile({
    required String firstName,
    required String lastName,
    required String name,
    required String phone,
    File? avatar,
  }) async {
    try {
      isLoading.value = true;

      // Gửi form-data lên server
      await _userRepository.updateProfileDio(
        firstName: firstName,
        lastName: lastName,
        name: name,
        phone: phone,
        avatarFile: avatar,
      );

      // Sau khi cập nhật thành công → gọi lại API để lấy dữ liệu mới
      await fetchUserProfile();

      ECustomSnackBar.showSuccess(
        title: 'Thành công',
        message: 'Đã cập nhật hồ sơ.',
      );
    } catch (e) {
      ECustomSnackBar.showError(
        title: 'Lỗi',
        message: 'Không thể cập nhật hồ sơ.',
      );
    } finally {
      isLoading.value = false;
    }
  }
}
