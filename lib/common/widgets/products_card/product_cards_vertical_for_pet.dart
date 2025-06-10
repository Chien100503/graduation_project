import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/products_card/favorite_icon/favorite_icon_pet.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/screens/pet_details/pet_detail.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../utils/constants/enums.dart';
import '../../../utils/helpers/helper_functions.dart';
import '../../styles/box_shadow.dart';
import '../custom_shape/containers/round_container.dart';
import '../images/round_images.dart';
import '../texts/bran_title_with_verify_icon.dart';
import '../texts/product_title_text.dart';

class EProductCardsVerticalForPet extends StatelessWidget {
  const EProductCardsVerticalForPet({super.key, required this.product});

  final PetDetailModel product;

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    return GestureDetector(
      onTap: () => Get.to(
            () => PetDetail(petId: product.id.toString()),
        transition: Transition.fadeIn,
        duration: const Duration(milliseconds: 500),
      ),
      child: Padding(
        padding: const EdgeInsets.only(bottom: 26),
        child: Container(
          width: 200,
          padding: const EdgeInsets.all(1),
          decoration: BoxDecoration(
            boxShadow: [EBoxShadow.verticalProductBoxShadow],
            borderRadius: BorderRadius.circular(ESizes.productImageRadius),
            color: dark ? EColors.accent : Colors.white,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ERoundContainer(
                height: 210,
                padding: const EdgeInsets.all(ESizes.sm),
                bg: dark ? EColors.thirdColor : EColors.cardLight,
                child: Stack(
                  children: [
                    Center(
                      child: ERoundImages(
                        boxFit: BoxFit.cover,
                        imageUrl: product.thumbnailUrl,
                        bg: Colors.transparent,
                        applyImageRadius: true,
                        isNetworkImage: true,
                      ),
                    ),
                    ERoundContainer(
                      radius: ESizes.sm,
                      padding: const EdgeInsets.symmetric(
                          horizontal: ESizes.sm, vertical: ESizes.xs),
                      bg: EColors.accent,
                      child: Text(
                        '${product.percentDiscount.toStringAsFixed(0)}%',
                        style: Theme.of(context)
                            .textTheme
                            .labelLarge!
                            .apply(color: EColors.thirdColor),
                      ),
                    ),
                    Positioned(
                      top: 0,
                      right: 0,
                      child: EFavoriteIconPet(petId: product.id.toString()),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: ESizes.defaultBetweenItem / 2),
              Padding(
                padding: const EdgeInsets.only(left: ESizes.sm),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    EProductTitleText(
                      title: product.name,
                      smallSize: true,
                    ),
                    EBrandTitleWithVerifyIcon(
                      title: product.breedName,
                      maxLines: 1,
                      brandTextSize: TextSizes.small,
                    ),
                    Row(
                      children: [
                        Text('\$${product.price}',
                            style: const TextStyle(
                                decoration: TextDecoration.lineThrough,
                                color: Colors.red)),
                        const SizedBox(width: ESizes.defaultBetweenItem),
                        Text(
                          '${product.priceDiscount}',
                          style: Theme.of(context).textTheme.headlineSmall,
                        ),
                      ],
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
