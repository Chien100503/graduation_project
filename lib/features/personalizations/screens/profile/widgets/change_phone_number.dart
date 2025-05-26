// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import 'package:iconsax/iconsax.dart';
// import 'package:pet_shop/features/personalizations/controllers/profile/phone_controller.dart';
//
// import '../../../../../common/widgets/appbar/appbar.dart';
// import '../../../../../utils/constants/sizes.dart';
// import '../../../../../utils/validators/validation.dart';
//
// class ChangePhoneNumber extends StatelessWidget {
//   const ChangePhoneNumber({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     final controller = Get.put(UpdatePhoneController());
//
//     return Scaffold(
//       appBar: const EAppBar(
//         title: Text('Change Phone number'),
//         showBackArrow: true,
//       ),
//       body: SingleChildScrollView(
//         child: Padding(
//           padding: const EdgeInsets.all(ESizes.defaultSpace),
//           child: Column(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: [
//               Text('Please enter your phone number in the box below',
//                   style: Theme.of(context).textTheme.titleMedium),
//               const SizedBox(height: ESizes.defaultBetweenSections),
//               Form(
//                 key: controller.updatePhoneFormKey,
//                 child: Column(
//                   children: [
//                     TextFormField(
//                       controller: controller.phone,
//                       validator: EValidation.validatePhoneNumber,
//                       keyboardType: TextInputType.phone,
//                       decoration: const InputDecoration(
//                         labelText: 'Phone number',
//                         prefixIcon: Icon(Iconsax.call),
//                       ),
//                     ),
//                     const SizedBox(height: ESizes.defaultBetweenSections),
//                     SizedBox(
//                       width: double.infinity,
//                       child: ElevatedButton(
//                         onPressed: controller.updatePhoneNumber,
//                         child: const Text('Save'),
//                       ),
//                     ),
//                   ],
//                 ),
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
// }
