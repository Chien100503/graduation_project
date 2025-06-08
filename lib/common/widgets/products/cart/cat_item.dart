// import 'package:flutter/material.dart';
// import '../../../../features/checkout/models/cart_item_model.dart';
// import '../../../../utils/constants/sizes.dart';
//
// class ECartItem extends StatelessWidget {
//   const ECartItem({
//     super.key,
//     required this.cartItem,
//   });
//
//   final CartItemModel cartItem;
//
//   @override
//   Widget build(BuildContext context) {
//     final itemName = cartItem.petName?.isNotEmpty == true
//         ? cartItem.petName!
//         : (cartItem.productName ?? 'Không có tên');
//
//     final itemQuantity = cartItem.quantity;
//     final itemTotalPrice = (cartItem.price * cartItem.quantity).toStringAsFixed(2);
//
//     return Row(
//       children: [
//         /// Tên sản phẩm hoặc pet + số lượng
//         Expanded(
//           child: Column(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: [
//               /// Tên
//               Text(
//                 itemName,
//                 style: Theme.of(context).textTheme.titleMedium,
//               ),
//
//               const SizedBox(height: ESizes.sm),
//
//               /// Số lượng
//               Text(
//                 'Số lượng: $itemQuantity',
//                 style: Theme.of(context).textTheme.bodySmall,
//               ),
//             ],
//           ),
//         ),
//
//         /// Giá
//         Text(
//           '$itemTotalPrice đ',
//           style: Theme.of(context).textTheme.titleMedium,
//         ),
//       ],
//     );
//   }
// }
