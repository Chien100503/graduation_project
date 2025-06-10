import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/screens/pet_details/pet_detail.dart';
import 'package:pet_shop/utils/constants/images_strings.dart';

import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../features/shop/models/products/pet_model.dart';
import '../../../utils/constants/enums.dart';
import '../../../utils/helpers/helper_functions.dart';
import '../custom_shape/containers/round_container.dart';
import '../images/round_images.dart';
import '../texts/bran_title_with_verify_icon.dart';
import '../texts/brand_title_text.dart';

class EProductCardsHorizontalForPet extends StatelessWidget {
  const EProductCardsHorizontalForPet({super.key, required this.pet});

  final PetDetailModel pet;

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return GestureDetector(
      onTap: () => Get.to(
            () => PetDetail(petId: pet.id.toString()),
        transition: Transition.fadeIn,
        duration: const Duration(milliseconds: 500),
      ),
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
                    imageUrl: pet.thumbnailUrl,
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
                        '${pet.percentDiscount.toStringAsFixed(0)}%',
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
                      title: 'Tên: ${pet.name}',
                      brandTextSize: TextSizes.medium,
                    ),
                    const SizedBox(height: ESizes.defaultBetweenItem / 2),
                    EBrandTitleWithVerifyIcon(title: pet.breed.name),
                    const SizedBox(height: ESizes.defaultBetweenItem / 2),
                    Row(
                      children: [
                        Text('Price ${pet.price.toInt()}', style: TextStyle(decoration: TextDecoration.lineThrough, color: Colors.red),),
                        SizedBox(width: 30,),
                        Text(
                          '${pet.priceDiscount}',
                          style: Theme.of(context).textTheme.headlineSmall,
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
