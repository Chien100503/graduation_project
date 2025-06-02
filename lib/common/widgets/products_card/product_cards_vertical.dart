import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../features/shop/models/products/pet_model.dart';
import '../../../utils/helpers/helper_functions.dart';
import '../custom_shape/containers/round_container.dart';
import '../images/round_images.dart';

class EProductCardsHorizontal extends StatelessWidget {
  const EProductCardsHorizontal({super.key, required this.product});

  final PetModel product;

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return GestureDetector(
      onTap: () {
        // Navigate to pet detail page
        // Get.to(() => PetDetailScreen(pet: product));
        print('Tapped on pet: ${product.name}');
      },
      child: Container(
        width: double.infinity,
        margin: const EdgeInsets.only(bottom: ESizes.defaultBetweenItem),
        padding: const EdgeInsets.all(ESizes.sm),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(ESizes.productImageRadius),
          color: dark ? EColors.accent : EColors.thirdColor,
          boxShadow: [
            BoxShadow(
              color: Colors.grey.withOpacity(0.1),
              spreadRadius: 1,
              blurRadius: 3,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Pet Image Section
            ERoundContainer(
              height: 120,
              width: 120,
              bg: dark ? EColors.thirdColor : EColors.cardLight,
              padding: const EdgeInsets.all(ESizes.sm),
              child: Stack(
                children: [
                  Center(
                    child: ERoundImages(
                      height: 100,
                      width: 100,
                      boxFit: BoxFit.cover,
                      applyImageRadius: true,
                      isNetworkImage: true,
                      imageUrl: product.imageUrls,
                      bg: Colors.transparent,
                    ),
                  ),
                  // Discount Badge (Optional)
                  Positioned(
                    left: 0,
                    top: 0,
                    child: ERoundContainer(
                      radius: ESizes.sm,
                      bg: EColors.secondary.withOpacity(0.8),
                      padding: const EdgeInsets.symmetric(
                        horizontal: ESizes.sm,
                        vertical: ESizes.xs,
                      ),
                      child: Text(
                        'NEW',
                        style: Theme.of(context)
                            .textTheme
                            .labelSmall!
                            .apply(color: EColors.thirdColor),
                      ),
                    ),
                  ),
                  // Favorite Icon
                  Positioned(
                    right: 0,
                    top: 0,
                    child: Container(
                      padding: const EdgeInsets.all(4),
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.8),
                        borderRadius: BorderRadius.circular(50),
                      ),
                      child: Icon(
                        Iconsax.heart,
                        size: 16,
                        color: Colors.grey[600],
                      ),
                    ),
                  ),
                ],
              ),
            ),

            // Pet Info Section
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(ESizes.sm),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Pet Name
                    Text(
                      product.name,
                      style: Theme.of(context).textTheme.titleMedium!.copyWith(
                        fontWeight: FontWeight.bold,
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),

                    const SizedBox(height: ESizes.defaultBetweenItem / 3),

                    // Breed Info
                    Row(
                      children: [
                        Icon(
                          Iconsax.category,
                          size: 14,
                          color: Colors.grey[600],
                        ),
                        const SizedBox(width: 4),
                        Text(
                          product.breed.name,
                          style: Theme.of(context).textTheme.bodySmall!.copyWith(
                            color: Colors.grey[600],
                          ),
                        ),
                      ],
                    ),

                    const SizedBox(height: ESizes.defaultBetweenItem / 2),

                    // Price and Add Button Row
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        // Price Section
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'Giá',
                              style: Theme.of(context).textTheme.bodySmall!.copyWith(
                                color: Colors.grey[600],
                              ),
                            ),
                            Text(
                              '\$${product.price.toStringAsFixed(0)}',
                              style: Theme.of(context).textTheme.titleLarge!.copyWith(
                                color: EColors.primaryColor,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),

                        // Add to Cart Button
                        GestureDetector(
                          onTap: () {
                            // Add to cart logic
                            print('Added ${product.name} to cart');
                            Get.snackbar(
                              'Thành công',
                              'Đã thêm ${product.name} vào giỏ hàng',
                              snackPosition: SnackPosition.BOTTOM,
                              backgroundColor: EColors.primaryColor,
                              colorText: Colors.white,
                              duration: const Duration(seconds: 2),
                            );
                          },
                          child: Container(
                            padding: const EdgeInsets.all(ESizes.sm),
                            decoration: BoxDecoration(
                              color: EColors.primaryColor,
                              borderRadius: BorderRadius.circular(ESizes.sm),
                              boxShadow: [
                                BoxShadow(
                                  color: EColors.primaryColor.withOpacity(0.3),
                                  spreadRadius: 1,
                                  blurRadius: 3,
                                  offset: const Offset(0, 2),
                                ),
                              ],
                            ),
                            child: const Icon(
                              Iconsax.add,
                              color: Colors.white,
                              size: 18,
                            ),
                          ),
                        ),
                      ],
                    ),

                    const SizedBox(height: ESizes.defaultBetweenItem / 3),

                    // Additional Info Row
                    Row(
                      children: [
                        // Pet ID
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.grey[200],
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            'ID: ${product.id}',
                            style: Theme.of(context).textTheme.bodySmall!.copyWith(
                              fontSize: 10,
                              color: Colors.grey[700],
                            ),
                          ),
                        ),

                        const Spacer(),

                        // Status or additional info
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.green[100],
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            'Có sẵn',
                            style: Theme.of(context).textTheme.bodySmall!.copyWith(
                              fontSize: 10,
                              color: Colors.green[700],
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
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