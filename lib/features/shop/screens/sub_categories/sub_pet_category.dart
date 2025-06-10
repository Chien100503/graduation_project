import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/common/widgets/images/round_images.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_horizontal_for_pet.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_horizontal_for_product.dart';
import 'package:pet_shop/common/widgets/shimmer/horizontal_product_shimmer.dart';
import 'package:pet_shop/common/widgets/texts/section_heading.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/pet_category_controller.dart';
import 'package:pet_shop/features/shop/models/categories/category_models.dart';
import 'package:pet_shop/utils/constants/images_strings.dart';
import 'package:pet_shop/utils/constants/sizes.dart';

import '../../../../utils/helpers/cloud_helper_functions.dart';

class SubPetCategoryScreen extends StatelessWidget {
  final int petCategoryId;
  final CategoryModel category;

  const SubPetCategoryScreen({
    super.key,
    required this.category,
    required this.petCategoryId,
  });

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(PetCategoryController());

    return Scaffold(
      appBar: EAppBar(
        title: Text(
          category.name,
        ),
        showBackArrow: true,
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(ESizes.defaultSpace),
          child: Column(
            children: [
              const ERoundImages(
                imageUrl: EImages.banner2,
                bg: Colors.transparent,
                applyImageRadius: true,
                width: double.infinity,
              ),
              const SizedBox(height: ESizes.defaultBetweenSections),
              FutureBuilder(
                future: controller.getBreeds(petCategoryId),
                builder: (context, snapshot) {
                  const loader = EHorizontalProductShimmer();
                  final widget = CloudHelperFunctions.checkMultiRecordState(
                      snapshot: snapshot, loader: loader);
                  if (widget != null) return widget;

                  final breeds = snapshot.data!;
                  return ListView.builder(
                    shrinkWrap: true,
                    itemCount: breeds.length,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (_, index) {
                      final breed = breeds[index];

                      return FutureBuilder(
                        future: controller.getPetsByBreed(petCategoryId, breed.id),
                        builder: (context, snapshot) {
                          final widget = CloudHelperFunctions.checkMultiRecordState(
                              snapshot: snapshot, loader: loader);
                          if (widget != null) return widget;

                          final pets = snapshot.data!;
                          print('${breed.name}');
                          return Column(
                            children: [
                              ESectionHeading(
                                title: 'Giống chó: ${breed.name}',
                                titleButton: 'View all',

                                // onPressed: () => Get.to(
                                //       () => AllProductScreen(
                                //     title: breed.name,
                                //     futureMethod: controller.fetchPetsByBreed(
                                //         petCategoryId, breed.id),
                                //   ),
                                // ),
                              ),
                              const SizedBox(height: ESizes.defaultBetweenItem / 2),
                              SizedBox(
                                height: 121,
                                child: ListView.separated(
                                  scrollDirection: Axis.horizontal,
                                  separatorBuilder: (_, __) => const SizedBox(
                                      width: ESizes.defaultBetweenItem),
                                  itemCount: pets.length,
                                  itemBuilder: (context, index) =>
                                      EProductCardsHorizontalForPet(pet: pets[index]),
                                ),
                              ),
                              const SizedBox(height: ESizes.defaultBetweenSections),
                            ],
                          );
                        },
                      );
                    },
                  );
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}
