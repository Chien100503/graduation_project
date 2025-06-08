import 'package:flutter/material.dart';
import 'package:pet_shop/features/shop/models/products/product_model.dart';
import 'package:pet_shop/utils/formatters/formatter.dart';

import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../features/shop/models/products/pet_model.dart';
import '../../../utils/constants/enums.dart';
import '../../../utils/helpers/helper_functions.dart';
import '../custom_shape/containers/round_container.dart';
import '../images/round_images.dart';
import '../texts/bran_title_with_verify_icon.dart';
import '../texts/brand_title_text.dart';

class EProductCardsHorizontalForProduct extends StatelessWidget {
  const EProductCardsHorizontalForProduct({super.key, required this.product});

  final ProductModel product;

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return GestureDetector(
      // onTap: () => Get.to(
      //       () => ProductDetail(product: product),
      //   transition: Transition.fadeIn,
      //   duration: const Duration(milliseconds: 500),
      // ),
      child: Container(
        width: 350,
        padding: const EdgeInsets.all(1),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(ESizes.productImageRadius),
          color: dark ? EColors.accent : EColors.thirdColor,
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ERoundContainer(
              height: 120,
              width: 120,
              bg: dark ? EColors.thirdColor : EColors.cardLight,
              padding: const EdgeInsets.all(ESizes.sm),
              child: Stack(
                children: [
                  ERoundImages(
                    height: 90,
                    width: 90,
                    boxFit: BoxFit.contain,
                    applyImageRadius: true,
                    isNetworkImage: true,
                    imageUrl: product.imageUrls,
                    bg: Colors.transparent,
                  ),
                  Positioned(
                    left: 0,
                    top: 0,
                    child: ERoundContainer(
                      radius: ESizes.sm,
                      bg: EColors.secondary.withOpacity(0.4),
                      padding: const EdgeInsets.symmetric(
                        horizontal: ESizes.sm,
                        vertical: ESizes.xs,
                      ),
                      child: Text(
                        '10%',
                        style: Theme.of(context)
                            .textTheme
                            .labelLarge!
                            .apply(color: EColors.thirdColor),
                      ),
                    ),
                  ),
                  // Positioned(
                  //   right: 0,
                  //   top: 0,
                  //   child: EFavoriteIcon(productId: product.id),
                  // ),
                ],
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(ESizes.sm),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    EBrandTitleText(
                      title: 'Tên: ${product.name}',
                      brandTextSize: TextSizes.medium,
                    ),
                    const SizedBox(height: ESizes.defaultBetweenItem / 2),
                    EBrandTitleWithVerifyIcon(title: product.type.name ?? ''),
                    const SizedBox(height: ESizes.defaultBetweenItem / 2),
                    // Text('Price ${EFormatter.priceFormatter(product.price)}'),
                    const SizedBox(height: ESizes.defaultBetweenItem / 2),
                    const Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        // Flexible(
                        //   child: Column(
                        //     crossAxisAlignment: CrossAxisAlignment.start,
                        //     children: [
                        //       if (product.productType == ProductType.single.toString() &&
                        //           product.salePrice > 0)
                        //         Padding(
                        //           padding: const EdgeInsets.only(left: ESizes.sm),
                        //           child: Text(
                        //             '\$${product.price}',
                        //             style: const TextStyle(
                        //               decoration: TextDecoration.lineThrough,
                        //               color: Colors.red,
                        //             ),
                        //           ),
                        //         ),
                        //       Padding(
                        //         padding: const EdgeInsets.only(left: ESizes.sm),
                        //         child: EProductPrice(
                        //           price: controller.getProductPrice(product),
                        //         ),
                        //       ),
                        //     ],
                        //   ),
                        // ),

                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
