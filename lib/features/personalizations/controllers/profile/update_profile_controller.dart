// import 'dart:io';
//
// import 'package:flutter/cupertino.dart';
// import 'package:get/get.dart';
// import 'package:get/get_core/src/get_main.dart';
// import 'package:get/get_state_manager/src/simple/get_controllers.dart';
//
// import '../../../../data/repositories/user_repository.dart';
//
// class UpdateProfileController extends GetxController {
//   final firstName = TextEditingController();
//   final lastName = TextEditingController();
//   final phone = TextEditingController();
//   File? avatarFile;
//
//   final formKey = GlobalKey<FormState>();
//
//   final _userRepository = UserRepository();
//
//   Future<void> updateProfile() async {
//     if (!formKey.currentState!.validate()) return;
//
//     try {
//       await _userRepository.updateProfileDio(
//         firstName: firstName.text.trim(),
//         lastName: lastName.text.trim(),
//         phone: phone.text.trim(),
//         avatarFile: avatarFile,
//       );
//
//       Get.snackbar('Thành công', 'Thông tin cá nhân đã được cập nhật');
//       Get.back();
//     } catch (e) {
//       Get.snackbar('Lỗi', 'Cập nhật không thành công');
//     }
//   }
//
//   @override
//   void onClose() {
//     firstName.dispose();
//     lastName.dispose();
//     phone.dispose();
//     super.onClose();
//   }
// }
