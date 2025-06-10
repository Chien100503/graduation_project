import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../common/widgets/custom_shape/containers/round_container.dart';
import '../../../../common/widgets/products/cart/cart_items.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../personalizations/controllers/address_controller/address_controller.dart';
import '../../controller/cart_controller/cart_controller.dart';
import '../../controller/checkout_controller.dart';
import 'widget/payment_section.dart';
import 'widget/address_section.dart';
import 'widget/amount.dart';

class PaymentScreen extends StatelessWidget {
  const PaymentScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    final cartController = CartController.instance;
    final checkoutController = CheckoutController.instance;

    return Scaffold(
      appBar: const EAppBar(title: Text('Order Review'), showBackArrow: true),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: SizedBox(
          width: double.infinity,
          child: ElevatedButton(
            onPressed: () {
              final address = AddressController.instance.selectedAddress.value;
              final paymentMethod = checkoutController.selectedPaymentMethod.value.name.trim().toUpperCase();

              if (address.id != null && paymentMethod.isNotEmpty) {
                checkoutController.createOrder(
                  addressId: address.id!,
                  paymentMethod: paymentMethod,
                );
              } else {
                Get.snackbar('Thiếu thông tin', 'Vui lòng chọn địa chỉ giao hàng và phương thức thanh toán');
              }
            },
            child: const Text('Xác nhận đặt hàng'),
          ),
        ),
      ),

      body: SingleChildScrollView(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: Column(
          children: [
            Obx(() => ECartItems(
              cartItems: cartController.cartItems.toList(),
              isEditing: false,
              selectedItems: const {},
              onItemSelected: (_, __) {},
            )),
            const SizedBox(height: ESizes.defaultBetweenSections),
            ERoundContainer(
              showBorder: true,
              bg: dark ? EColors.primaryColor : EColors.thirdColor,
              padding: EdgeInsets.all(ESizes.md),
              child: Column(
                children: [
                  BillingPaymentSections(),
                  BillingPaymentSection(),
                  AddressSection(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
