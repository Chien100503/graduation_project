import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/common/widgets/texts/product_title_text.dart';
import 'package:pet_shop/features/shop/controllers/products/pet_controller.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/bottom_navigation_detail.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/product_attributes.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/product_image_slider.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/rate_and_share.dart';

import '../../../../common/widgets/images/circle_images.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';
import '../../controllers/products/product_controller.dart';


class ProductDetail extends StatelessWidget {
  const ProductDetail({super.key, required this.product});

  final ProductDetailModel product;

  @override
  Widget build(BuildContext context) {
    final controller = ProductController.instance;
    final dark = EHelperFunctions.isDarkMode(context);
    return Scaffold(
      // bottomNavigationBar: EBottomNavigationDetail(product: product),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // image product - slider
            EProductImageSlider(product: product),
            // Rate and share
            // const ERateAndShare(),
            // Sale and price
            Padding(
              padding: const EdgeInsets.only(
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
                bottom: ESizes.defaultSpace,
              ),
              child: Row(
                children: [
                  Container(
                    height: 22,
                    width: 33,
                    decoration: BoxDecoration(
                      color: dark ? EColors.accent : EColors.thirdColor,
                      borderRadius: BorderRadius.circular(5),
                    ),
                    child: Center(
                      child: Text('sale',
                          style: Theme
                              .of(context)
                              .textTheme
                              .labelSmall),
                    ),
                  ),
                  const SizedBox(width: ESizes.defaultBetweenItem),
                  Text(
                    '\$${product.price}',
                    style: const TextStyle(
                        decoration: TextDecoration.lineThrough,
                        color: Colors.red,
                        fontWeight: FontWeight.w600,
                        fontSize: 16),
                  ),
                  const SizedBox(width: ESizes.defaultSpace),
                  // EProductPrice(price: controller.getProductPrice(product)),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: ESizes.defaultSpace, right: ESizes.defaultSpace),
              child: Text('• ${product.name}',
                  style: Theme
                      .of(context)
                      .textTheme
                      .bodyLarge),
            ),
            const SizedBox(
              height: ESizes.defaultBetweenItem,
            ),
            // Brand product
            Padding(
              padding: const EdgeInsets.only(
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
                bottom: ESizes.defaultSpace,
              ),
              child: Row(
                children: [
                  Text('Status: ',
                      style: Theme
                          .of(context)
                          .textTheme
                          .titleLarge),
                  //Stock
                  Text(controller.getProductStockStatus(product.stockQuantity),style: Theme
                      .of(context)
                      .textTheme
                      .titleLarge)
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
                bottom: ESizes.defaultSpace,
              ),
              child: Row(
                children: [
                  EProductTitleText(title: product.brandName),
                  // ECircleImage(
                  //   image: product.breedName != null ? product.br!.image : '',
                  //   isNetworkImage: true,
                  //   bg: EColors.thirdColor,),
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        // Text(product.brand != null ? product.brand!.name : '',
                        //   style: Theme
                        //       .of(context)
                        //       .textTheme
                        //       .bodyLarge,),
                        const Icon(
                          Iconsax.verify5, size: 20, color: Colors.blueAccent,)
                      ],
                    ),
                  ),
                ],
              ),
            ),

            // Padding(
            //   padding: const EdgeInsets.only(
            //     left: ESizes.defaultSpace,
            //     right: ESizes.defaultSpace,
            //     bottom: ESizes.defaultSpace,
            //   ),
            //   child: EProductAttributes(product: product),
            // ),
            // Options
            // if(product.productType == ProductType.variable.toString())
            // EDescription(description: product.description ?? '',),

            Divider(
                thickness: 1,
                color: dark ? EColors.thirdColor : EColors.primaryColor),
            const SizedBox(height: ESizes.defaultBetweenItem),
            Padding(
              padding: const EdgeInsets.only(
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
                bottom: ESizes.defaultSpace,
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const ESectionHeading(
                    title: 'Review',
                    showActionButton: false,
                  ),
                  // IconButton(
                  //   onPressed: () =>
                  //       Get.to(() => const ReviewAndRating(),
                  //           duration: const Duration(milliseconds: 300),
                  //           transition: Transition.rightToLeftWithFade),
                  //   icon: Icon(
                  //     Icons.arrow_forward_ios_rounded,
                  //     color: dark ? EColors.thirdColor : EColors.primaryColor,
                  //   ),
                  // )
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
