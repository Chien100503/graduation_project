import 'package:flutter/material.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import '../../../../features/checkout/models/cart_item_model.dart';

class ECartItems extends StatelessWidget {
  final List<CartItemModel> cartItems;
  final bool isEditing;
  final Set<CartItemModel> selectedItems;
  final void Function(CartItemModel item, bool? selected)? onItemSelected;

  const ECartItems({
    super.key,
    required this.cartItems,
    required this.isEditing,
    required this.selectedItems,
    this.onItemSelected,
  });

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    return Column(
      children: cartItems.map((item) {
        final name = item.productName ?? item.petName ?? 'No Name';
        final imageUrl = item.thumbnailUrl;

        return Card(
          elevation: 4,
          color: dark ? EColors.primaryColor : EColors.thirdColor,
          margin: const EdgeInsets.symmetric(vertical: 10, horizontal: 12),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),

          ),
          child: Padding(
            padding: const EdgeInsets.all(12.0),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Image
                ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: imageUrl != null
                      ? Image.network(
                    imageUrl,
                    width: 100,
                    height: 100,
                    fit: BoxFit.cover,
                    errorBuilder: (context, error, stackTrace) =>
                    const Icon(Icons.image_not_supported, size: 40),
                  )
                      : const Icon(Icons.pets, size: 40),
                ),
                const SizedBox(width: 12),
                // Info
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        name,
                        style: Theme.of(context)
                            .textTheme
                            .titleMedium
                            ?.copyWith(fontWeight: FontWeight.bold),
                      ),
                      if (item.brandName != null)
                        Text('Thương hiệu: ${item.brandName}', style: Theme.of(context).textTheme.labelMedium,),
                      if (item.breedName != null)
                        Text('Giống: ${item.breedName}', style: Theme.of(context).textTheme.titleLarge,),
                      const SizedBox(height: 6),
                      Text('Giá: \$${item.price.toStringAsFixed(2)}', style: Theme.of(context).textTheme.labelMedium,),
                      Text('Số lượng: ${item.quantity}', style: Theme.of(context).textTheme.labelMedium,),
                      Text(
                        'Tổng: \$${item.itemTotalPrice.toStringAsFixed(2)}', style: Theme.of(context).textTheme.titleSmall,
                      ),
                    ],
                  ),
                ),
                // Checkbox khi đang chỉnh sửa
                if (isEditing)
                  Checkbox(
                    value: selectedItems.contains(item),
                    onChanged: (value) => onItemSelected?.call(item, value),
                    activeColor: Colors.deepPurple,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(4),
                    ),
                  ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }
}
