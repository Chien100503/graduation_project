import 'package:get/get.dart';
import '../../../../data/repositories/user_repository.dart';
import '../../models/user_model.dart';

class UserController extends GetxController {
  static UserController get instance => Get.find();

  final _userRepository = UserRepository();
  var profile = UserProfileModel.empty().obs;
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
      print('❌ Lỗi khi lấy profile: $e');
    } finally {
      isLoading.value = false;
    }
  }
}
