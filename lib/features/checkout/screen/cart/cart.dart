import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/checkout/screen/payment/payment.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../common/widgets/loader/animation_loader_widget.dart';
import '../../../../common/widgets/products/cart/cart_items.dart';
import '../../../../navigation_menu.dart';
import '../../../../utils/constants/images_strings.dart';
import '../../../../utils/constants/sizes.dart';
import '../../controller/cart_controller/cart_controller.dart';
import '../../models/cart_item_model.dart';

class CartScreen extends StatefulWidget {
  const CartScreen({super.key, this.cartItems});
  final List<CartItemModel>? cartItems;

  @override
  _CartScreenState createState() => _CartScreenState();
}

class _CartScreenState extends State<CartScreen> {
  bool isEditing = false;
  final Set<CartItemModel> selectedItems = {};
  final controller = CartController.instance;

  @override
  void initState() {
    super.initState();
    controller.fetchCartItems();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: EAppBar(
        title: Text('Cart', style: Theme.of(context).textTheme.headlineSmall),
        showBackArrow: true,
        actions: [
          TextButton(
            child: Text(
              isEditing ? 'Done' : 'Select All',
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            onPressed: () {
              setState(() {
                isEditing = !isEditing;
                selectedItems.clear();
              });
            },
          ),
        ],
      ),
      body: Obx(() {
        final isEmpty = controller.cartItems.isEmpty;
        if (isEmpty) {
          return EAnimationLoaderWidget(
            text: 'Whoops!, Cart is empty',
            animation: EImages.cartNull,
            showAction: true,
            actionText: 'Let\'s fill it',
            onActionPress: () => Get.off(() => const NavigationMenu()),
          );
        } else {
          return SingleChildScrollView(
            physics: const BouncingScrollPhysics(),
            padding: const EdgeInsets.all(ESizes.defaultSpace),
            child: ECartItems(
              cartItems: controller.cartItems.toList(),
              isEditing: isEditing,
              selectedItems: selectedItems,
              onItemSelected: (item, selected) {
                setState(() {
                  if (selected == true) {
                    selectedItems.add(item);
                  } else {
                    selectedItems.remove(item);
                  }
                });
              },
            ),
          );
        }
      }),
      bottomNavigationBar: Obx(() {
        final isEmpty = controller.cartItems.isEmpty;

        if (isEmpty) return const SizedBox();

        return Padding(
          padding: const EdgeInsets.all(ESizes.defaultSpace),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              if (isEditing)
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      for (final item in selectedItems) {
                        controller.updateItemQuantity(item.id, 0);
                      }
                      setState(() {
                        selectedItems.clear();
                        isEditing = false;
                      });
                    },
                    child: const Text('Delete Selected'),
                  ),
                ),
              if (isEditing) const SizedBox(width: 8),
              if (isEditing)
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      controller.clearCart();
                      setState(() {
                        selectedItems.clear();
                        isEditing = false;
                      });
                    },
                    child: const Text('Delete All'),
                  ),
                ),
              if (!isEditing)
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => Get.to(
                          () => const PaymentScreen(),
                      transition: Transition.rightToLeftWithFade,
                      duration: const Duration(milliseconds: 400),
                    ),
                    child: const Text('Checkout'),
                  ),
                ),
            ],
          ),
        );
      }),
    );
  }
}
