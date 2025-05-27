import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/shop/models/products/pet_model.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/constants/sizes.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import 'package:pet_shop/common/widgets/custom_shape/containers/round_container.dart';
import 'package:pet_shop/common/widgets/images/round_images.dart';
import 'package:pet_shop/common/widgets/texts/product_title_text.dart';

class EProductCardVertical extends StatelessWidget {
  const EProductCardVertical({super.key, required this.pet});

  final PetModel pet;

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return GestureDetector(

      // onTap: () => Get.to(
      //       () => PetDetail(pet: pet),
      //   transition: Transition.fadeIn,
      //   duration: const Duration(milliseconds: 500),
      // ),
      child: Padding(
        padding: const EdgeInsets.only(bottom: 26),
        child: Container(
          width: 200,
          padding: const EdgeInsets.all(1),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(ESizes.productImageRadius),
            color: dark ? EColors.accent : Colors.white,
            boxShadow: [
              BoxShadow(
                color: Colors.black12,
                blurRadius: 6,
                offset: Offset(0, 3),
              ),
            ],
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ERoundContainer(
                height: 178,
                padding: const EdgeInsets.all(ESizes.sm),
                bg: dark ? EColors.thirdColor : EColors.cardLight,
                child: Stack(
                  children: [
                    Center(
                      child: ERoundImages(
                        boxFit: BoxFit.cover,
                        imageUrl: pet.imageUrl,
                        bg: Colors.transparent,
                        applyImageRadius: true,
                        isNetworkImage: true,
                      ),
                    ),
                    // Positioned(
                    //   top: 0,
                    //   right: 0,
                    //   child: EFavoriteIcon(productId: pet.id),
                    // ),
                  ],
                ),
              ),
              const SizedBox(height: ESizes.defaultBetweenItem / 2),
              Padding(
                padding: const EdgeInsets.only(left: ESizes.sm, right: ESizes.sm),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    EProductTitleText(
                      title: pet.name,
                      smallSize: true,
                    ),
                    const SizedBox(height: ESizes.xs),
                    Text(
                      pet.name,
                      style: Theme.of(context)
                          .textTheme
                          .labelMedium!
                          .copyWith(color: Colors.grey),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: ESizes.sm),
                    Text(
                      '\$${pet.price.toStringAsFixed(2)}',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
