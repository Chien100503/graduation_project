// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import 'package:pet_shop/features/personalizations/controllers/profile/user_controller.dart';
//
// class UpdatePhoneController extends GetxController {
//   final phone = TextEditingController();
//   final updatePhoneFormKey = GlobalKey<FormState>();
//
//   void updatePhoneNumber() {
//     if (!updatePhoneFormKey.currentState!.validate()) return;
//
//     // ✅ Cập nhật local UI
//     UserController.instance.updatePhoneLocally(phone.text.trim());
//
//     // 👉 Có thể gọi API cập nhật thực tế nếu muốn
//     Get.back(); // Đóng màn hình sau khi cập nhật
//     Get.snackbar('Success', 'Phone number updated');
//   }
//
//   @override
//   void onClose() {
//     phone.dispose();
//     super.onClose();
//   }
// }
