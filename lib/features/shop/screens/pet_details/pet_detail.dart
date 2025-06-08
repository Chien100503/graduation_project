import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/common/widgets/chips/chip_color.dart';
import 'package:pet_shop/common/widgets/texts/product_title_text.dart';
import 'package:pet_shop/features/checkout/screen/cart/cart.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/screens/pet_details/widget/bottom_navigation_detail.dart';
import 'package:pet_shop/features/shop/screens/pet_details/widget/pet_image_slider.dart';

import '../../../../common/widgets/custom_shape/circle_container.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';
import '../../../checkout/controller/cart_controller/cart_controller.dart';
import '../../../checkout/models/cart_item_model.dart';
import '../../controllers/products/product_controller.dart';
import '../product_details/widget/description.dart';


class PetDetail extends StatelessWidget {
  const PetDetail({super.key, required this.product, this.cartItems});

  final List<CartItemModel>? cartItems;
  final PetDetailModel product;

  @override
  Widget build(BuildContext context) {
    final cartController = CartController.instance;
    final controller = ProductController.instance;
    final isColor = EHelperFunctions.getColor(product.color) != null;
    final chipColor = isColor ? EHelperFunctions.getColor(product.color)! : null;
    final dark = EHelperFunctions.isDarkMode(context);
    return Scaffold(
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: ElevatedButton.icon(
          icon: const Icon(Iconsax.shopping_bag),
          label: const Text('Thêm vào giỏ hàng'),
          style: ElevatedButton.styleFrom(
            backgroundColor: EColors.primaryColor,
            padding: const EdgeInsets.symmetric(vertical: 16),
          ),
          onPressed: () {
            cartController.addPetToCart(product);
            Get.snackbar(
              'Thành công',
              'Đã thêm sản phẩm vào giỏ hàng',
              backgroundColor: Colors.green,
              colorText: Colors.white,
              snackPosition: SnackPosition.TOP,
              icon: const Icon(Icons.check_circle, color: Colors.white),
              borderRadius: 8,
              margin: EdgeInsets.all(16),
              duration: Duration(seconds: 2),
            );

          },
        ),
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // image product - slider
            EPetImageSlider(product: product),
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
              child: EProductTitleText(title: '- Tên thú cưng: ${product.name}'),
            ),
            Padding(
              padding: const EdgeInsets.only(
                top: ESizes.defaultSpace,
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace
              ),
              child: Row(
                children: [
                  EProductTitleText(title:'Giống loài: ${product.breedName}'),
                ],
              ),
            ),
            const Padding(
              padding: EdgeInsets.only(
                  left: ESizes.defaultSpace, right: ESizes.defaultSpace, top: ESizes.defaultSpace),
              child:ESectionHeading(title: 'Cân nặng', showActionButton: false,),
            ),
            const SizedBox(height: ESizes.defaultBetweenItem,),
            Padding(
              padding: const EdgeInsets.only(
                  left: ESizes.defaultSpace, right: ESizes.defaultSpace),

              child: EChipColor(text: '${product.size} kg', selected: false, onSelected: (value){},),
            ),
            const Padding(
              padding: EdgeInsets.only(
                  left: ESizes.defaultSpace, right: ESizes.defaultSpace,top: ESizes.defaultSpace),
              child:ESectionHeading(title: 'Màu', showActionButton: false,),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: ESizes.defaultSpace, right: ESizes.defaultSpace),

              child: ChoiceChip(
                label: isColor ? const SizedBox() : Text(product.color),
                selected: false,
                onSelected: (value){},
                selectedColor: chipColor,
                labelStyle: TextStyle(color: chipColor),
                shape: isColor ? const CircleBorder() : null,
                avatar: isColor
                    ? ECircleContainer(
                    width: 50,
                    height: 50,
                    backgroundColor: EHelperFunctions.getColor(product.color)!)
                    : null,
                labelPadding: isColor ? const EdgeInsets.all(0) : null,
                padding: isColor ? const EdgeInsets.all(0) : null,
                backgroundColor: isColor ? EHelperFunctions.getColor(product.color)! : dark ? EColors.accent : EColors.thirdColor,
              ),
            ),
            const SizedBox(
              height: ESizes.defaultBetweenItem,
            ),
            EDescription(description: product.description),
            Divider(
                thickness: 1,
                color: dark ? EColors.thirdColor : EColors.primaryColor),
            const SizedBox(height: ESizes.defaultBetweenItem),
            const Padding(
              padding: EdgeInsets.only(
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
                bottom: ESizes.defaultSpace,
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  ESectionHeading(
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
