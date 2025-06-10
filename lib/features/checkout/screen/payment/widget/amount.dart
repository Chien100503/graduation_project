import 'package:flutter/material.dart';

import '../../../controller/cart_controller/cart_controller.dart';

class BillingPaymentSections extends StatelessWidget {
  const BillingPaymentSections({super.key});

  @override
  Widget build(BuildContext context) {
    final cartController = CartController.instance;
    // final subTotal = cartController.totalCartPrice.value;
    final totalSale = cartController.cartItems.fold<double>(
      0.0,
          (previousValue, item) => previousValue + (item.priceDiscount * item.quantity),
    );
    final total = cartController.cartItems.fold<double>(
      0.0,
          (previousValue, item) => previousValue + (item.price * item.quantity),
    );

    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: Text('Total Sale',
                  style: Theme.of(context).textTheme.headlineSmall),
            ),
            Text(totalSale.toStringAsFixed(2), style: Theme.of(context).textTheme.titleMedium),
          ],
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: Text('Subtotal',
                  style: Theme.of(context).textTheme.headlineSmall),
            ),
            Text(total.toStringAsFixed(2), style: Theme.of(context).textTheme.titleMedium),
          ],
        ),

        const Divider(),
      ],
    );
  }
}
