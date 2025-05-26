// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import 'package:iconsax/iconsax.dart';
// import 'package:pet_shop/utils/constants/sizes.dart';
//
// import '../../../controllers/profile/updateName_controller.dart';
//
// class ChangeName extends StatelessWidget {
//   final controller = Get.put(UpdateNameController());
//
//   ChangeName({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     // Không cần gọi loadCurrentUser ở đây nếu đã gọi trong onInit
//     return Scaffold(
//       appBar: AppBar(title: const Text('Cập nhật tên')),
//       body: Obx(() {
//         return controller.isLoading.value
//             ? const Center(child: CircularProgressIndicator())
//             : Padding(
//           padding: const EdgeInsets.all(ESizes.defaultSpace),
//           child: Form(
//             key: controller.formKey,
//             child: Column(
//               children: [
//                 TextFormField(
//                   controller: controller.firstNameController,
//                   decoration: const InputDecoration(
//                     labelText: 'Họ',
//                     prefixIcon: Icon(Iconsax.user),
//                   ),
//                   // validator: TValidator.validateName,
//                 ),
//                 const SizedBox(height: ESizes.defaultBetweenItem),
//                 TextFormField(
//                   controller: controller.lastNameController,
//                   decoration: const InputDecoration(
//                     labelText: 'Tên',
//                     prefixIcon: Icon(Iconsax.user_edit),
//                   ),
//                   // validator: TValidator.validateName,
//                 ),
//                 const SizedBox(height: ESizes.defaultBetweenSections),
//                 SizedBox(
//                   width: double.infinity,
//                   child: ElevatedButton(
//                     onPressed: controller.updateName,
//                     child: const Text('Lưu thay đổi'),
//                   ),
//                 ),
//               ],
//             ),
//           ),
//         );
//       }),
//     );
//   }
// }
