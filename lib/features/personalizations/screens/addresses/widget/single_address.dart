// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import 'package:iconsax/iconsax.dart';
//
// import '../../../../../common/widgets/custom_shape/containers/round_container.dart';
// import '../../../../../utils/constants/colors.dart';
// import '../../../../../utils/constants/sizes.dart';
// import '../../../../../utils/helpers/helper_functions.dart';
// import '../../../controllers/address_controller/address_controller.dart';
// import '../../../models/address_model.dart';
//
// class SingleAddress extends StatelessWidget {
//   const SingleAddress({
//     super.key,
//     required this.address,
//     required this.onTap,
//     this.onEdit,
//     this.onDelete,
//   });
//
//   final AddressModel address;
//   final VoidCallback onTap;
//   final VoidCallback? onEdit;
//   final Function(String)? onDelete;
//
//   @override
//   Widget build(BuildContext context) {
//     final controller = AddressController.instance;
//     final dark = EHelperFunctions.isDarkMode(context);
//
//     return Obx(() {
//       final selectedAddressId = controller.selectedAddress.value.id;
//       final selectedAddress = selectedAddressId == address.id;
//
//       return GestureDetector(
//         onTap: onTap,
//         onDoubleTap: () {
//           _showOptionsDialog(context);
//         },
//         child: ERoundContainer(
//           padding: const EdgeInsets.all(ESizes.md),
//           width: double.infinity,
//           showBorder: true,
//           bg: selectedAddress
//               ? EColors.thirdColor.withOpacity(0.5)
//               : Colors.transparent,
//           borderColor: selectedAddress
//               ? Colors.transparent
//               : dark
//               ? EColors.primaryColor
//               : Colors.grey,
//           margin: const EdgeInsets.only(bottom: ESizes.defaultBetweenItem),
//           child: Stack(
//             children: [
//               // Selection indicator
//               Positioned(
//                 top: 0,
//                 right: 5,
//                 child: Icon(
//                   selectedAddress ? Iconsax.tick_circle5 : null,
//                   color: selectedAddress
//                       ? dark
//                       ? EColors.thirdColor
//                       : EColors.primaryColor
//                       : null,
//                 ),
//               ),
//
//               // Default badge
//               if (address.isDefault)
//                 Positioned(
//                   top: 0,
//                   left: 5,
//                   child: Container(
//                     padding: const EdgeInsets.symmetric(
//                       horizontal: ESizes.sm,
//                       vertical: ESizes.xs,
//                     ),
//                     decoration: BoxDecoration(
//                       color: dark ? EColors.thirdColor : EColors.primaryColor,
//                       borderRadius: BorderRadius.circular(ESizes.sm),
//                     ),
//                     child: Text(
//                       'Default',
//                       style: Theme.of(context).textTheme.labelSmall?.copyWith(
//                         color: dark
//                             ? EColors.primaryColor
//                             : EColors.thirdColor,
//                         fontWeight: FontWeight.bold,
//                       ),
//                     ),
//                   ),
//                 ),
//
//               // Address content
//               Padding(
//                 padding: EdgeInsets.only(
//                   top: address.isDefault ? ESizes.lg : 0,
//                   right: ESizes.lg,
//                 ),
//                 child: Column(
//                   crossAxisAlignment: CrossAxisAlignment.start,
//                   children: [
//                     // Name
//                     Text(
//                       address.name,
//                       style: Theme.of(context).textTheme.headlineSmall,
//                       maxLines: 1,
//                       overflow: TextOverflow.ellipsis,
//                     ),
//
//                     const SizedBox(height: ESizes.defaultBetweenItem),
//
//                     // Full Address
//                     Row(
//                       children: [
//                         Icon(
//                           Icons.location_on_outlined,
//                           size: 16,
//                           color: dark
//                               ? EColors.thirdColor
//                               : EColors.primaryColor,
//                         ),
//                         const SizedBox(width: ESizes.xs),
//                         Expanded(
//                           child: Text(
//                             address.fullAddress,
//                             maxLines: 2,
//                             overflow: TextOverflow.ellipsis,
//                             style: Theme.of(context).textTheme.titleLarge,
//                           ),
//                         ),
//                       ],
//                     ),
//
//                     const SizedBox(height: ESizes.defaultBetweenItem / 2),
//
//                     // Phone Number
//                     Row(
//                       children: [
//                         Icon(
//                           Icons.phone_outlined,
//                           size: 16,
//                           color: dark
//                               ? EColors.thirdColor
//                               : EColors.primaryColor,
//                         ),
//                         const SizedBox(width: ESizes.xs),
//                         Text(
//                           address.phone,
//                           maxLines: 1,
//                           overflow: TextOverflow.ellipsis,
//                           style: Theme.of(context).textTheme.titleMedium,
//                         ),
//                       ],
//                     ),
//
//                     // Set as default button (if not default)
//                     if (!address.isDefault)
//                       Align(
//                         alignment: Alignment.centerRight,
//                         child: TextButton.icon(
//                           onPressed: () async {
//                             await _setAsDefault(context);
//                           },
//                           icon: const Icon(Iconsax.star),
//                           label: const Text('Đặt làm mặc định'),
//                           style: TextButton.styleFrom(
//                             foregroundColor: dark
//                                 ? EColors.thirdColor
//                                 : EColors.primaryColor,
//                             textStyle: const TextStyle(
//                                 fontWeight: FontWeight.bold),
//                           ),
//                         ),
//                       ),
//                   ],
//                 ),
//               ),
//             ],
//           ),
//         ),
//       );
//     });
//   }
//
//   void _showOptionsDialog(BuildContext context) async {
//     final dark = EHelperFunctions.isDarkMode(context);
//
//     final result = await showDialog<String>(
//       context: context,
//       builder: (BuildContext context) {
//         return AlertDialog(
//           title: Text(
//             'Address Options',
//             style: TextStyle(
//               color: dark ? EColors.thirdColor : EColors.primaryColor,
//             ),
//           ),
//           content: Column(
//             mainAxisSize: MainAxisSize.min,
//             children: [
//               // Edit option
//               ListTile(
//                 leading: Icon(
//                   Icons.edit,
//                   color: dark ? EColors.thirdColor : EColors.primaryColor,
//                 ),
//                 title: const Text('Edit'),
//                 onTap: () {
//                   Navigator.of(context).pop('edit');
//                 },
//               ),
//
//               // Set as default option (only if not already default)
//               if (!address.isDefault)
//                 ListTile(
//                   leading: Icon(
//                     Icons.star_outline,
//                     color: dark ? EColors.thirdColor : EColors.primaryColor,
//                   ),
//                   title: const Text('Set as Default'),
//                   onTap: () {
//                     Navigator.of(context).pop('default');
//                   },
//                 ),
//
//               // Delete option (only if not default)
//               if (!address.isDefault)
//                 ListTile(
//                   leading: const Icon(
//                     Icons.delete,
//                     color: Colors.red,
//                   ),
//                   title: const Text('Delete'),
//                   onTap: () {
//                     Navigator.of(context).pop('delete');
//                   },
//                 ),
//             ],
//           ),
//           backgroundColor: dark ? EColors.primaryColor : Colors.white,
//           elevation: 4.0,
//         );
//       },
//     );
//
//     // Handle the result
//     if (result == 'edit' && onEdit != null) {
//       onEdit!();
//     } else if (result == 'delete' && onDelete != null) {
//       onDelete!(address.id.toString());
//     } else if (result == 'default') {
//       await _setAsDefault(context);
//     }
//   }
//
//   Future<void> _setAsDefault(BuildContext context) async {
//     final dark = EHelperFunctions.isDarkMode(context);
//     try {
//       await AddressController.instance.addressRepository
//           .setDefaultAddress(address.id.toString());
//
//       AddressController.instance.refreshData.toggle();
//
//       Get.snackbar(
//         'Thành công',
//         'Đã cập nhật địa chỉ mặc định',
//         snackPosition: SnackPosition.BOTTOM,
//         backgroundColor: dark ? EColors.thirdColor : EColors.primaryColor,
//         colorText: dark ? EColors.primaryColor : EColors.thirdColor,
//       );
//     } catch (e) {
//       Get.snackbar(
//         'Lỗi',
//         'Không thể cập nhật địa chỉ mặc định',
//         snackPosition: SnackPosition.BOTTOM,
//         backgroundColor: Colors.red,
//         colorText: Colors.white,
//       );
//     }
//   }
// }
