ximport 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../utils/constants/colors.dart';

class ProductCardAddToCartButton extends StatelessWidget {
  const ProductCardAddToCartButton({super.key, required this.product});

  final ProductModel product;

  @override
  Widget build(BuildContext context) {
    final cartController = CartController.instance;
    final dark = EHelperFunctions.isDarkMode(context);
    return Obx(
      () {
        final productQuantityInCart =
            cartController.getProductQuantityInCart(product.id);
        return Container(
          height: 40,
          width: 40,
          decoration: BoxDecoration(
              borderRadius: const BorderRadius.only(
                bottomRight: Radius.circular(40),
                topLeft: Radius.circular(100),
              ),
              color: productQuantityInCart > 0
                  ? dark ? EColors.primaryColor : EColors.thirdColor
                  : Colors.transparent),
          child: productQuantityInCart > 0
              ? Padding(
                padding: const EdgeInsets.only(top: 10, left: 20),
                child: Text(
                    productQuantityInCart.toString(),
                    style: Theme.of(context)
                        .textTheme
                        .bodyLarge
                  ),
              )
              : null,
        );
      },
    );
  }
}
