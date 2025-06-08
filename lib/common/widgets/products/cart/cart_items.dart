import 'package:flutter/material.dart';
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
    return Column(
      children: cartItems.map((item) {
        final name = item.productName ?? item.petName ?? 'No Name';
        return Card(
          margin: const EdgeInsets.symmetric(vertical: 8),
          child: ListTile(
            leading: const Icon(Icons.shopping_cart),
            title: Text(name, style: Theme.of(context).textTheme.titleMedium),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Price: \$${item.price.toStringAsFixed(2)}'),
                Text('Quantity: ${item.quantity}'),
                Text('Total: \$${item.itemTotalPrice.toStringAsFixed(2)}'),
              ],
            ),
            trailing: isEditing
                ? Checkbox(
              value: selectedItems.contains(item),
              onChanged: (value) => onItemSelected?.call(item, value),
            )
                : null,
          ),
        );
      }).toList(),
    );
  }
}
