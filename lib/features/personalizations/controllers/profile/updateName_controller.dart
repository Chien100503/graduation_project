// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import '../../../../data/repositories/user_repository.dart';
// import '../../../personalizations/controllers/profile/user_controller.dart';
//
// class UpdateNameController extends GetxController {
//   final firstNameController = TextEditingController();
//   final lastNameController = TextEditingController();
//   final formKey = GlobalKey<FormState>();
//
//   final isLoading = false.obs;
//
//   final _userRepository = UserRepository();
//
//   // Load dữ liệu người dùng hiện tại để gán vào TextField
//   void loadCurrentUser() {
//     final user = UserController.instance.profile.value;
//     firstNameController.text = user.firstName;
//     lastNameController.text = user.lastName;
//   }
//
//   // Hàm cập nhật tên người dùng
//   Future<void> updateName() async {
//     if (!formKey.currentState!.validate()) return;
//
//     try {
//       isLoading.value = true;
//       final firstName = firstNameController.text.trim();
//       final lastName = lastNameController.text.trim();
//
//       // ✅ Gọi API với dữ liệu đầy đủ
//       await _userRepository.updateProfileDio(
//         firstName: firstName,
//         lastName: lastName,
//       );
//
//       // ✅ Cập nhật UI
//       UserController.instance.updateNameLocally(
//         firstName: firstName,
//         lastName: lastName,
//       );
//
//       Get.back();
//       Get.snackbar('Thành công', 'Đã cập nhật tên của bạn');
//     } catch (e) {
//       Get.snackbar('Lỗi', 'Không thể cập nhật tên');
//     } finally {
//       isLoading.value = false;
//     }
//   }
// }
