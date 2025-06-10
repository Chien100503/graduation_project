import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_pet.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/category_controller.dart';
import 'package:pet_shop/features/shop/models/categories/breed_model.dart';
import 'package:pet_shop/features/shop/screens/stores/widget/category_breed.dart';

import '../../../../../common/widgets/layouts/grid_layout.dart';
import '../../../../../common/widgets/shimmer/vertical_product_card_shimmer.dart';
import '../../../../../common/widgets/texts/section_heading.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../../utils/helpers/cloud_helper_functions.dart';
import '../../../models/categories/category_models.dart';

class ECategoryTabPets extends StatelessWidget {
  const ECategoryTabPets({super.key, required this.category});

  final CategoryModel category;

  @override
  Widget build(BuildContext context) {
    final controller = CategoryController.instance;
    return ListView(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      children: [
        Padding(
          padding: const EdgeInsets.all(ESizes.defaultSpace),
          child: Column(
            children: [
              // CategoryBreed(breed: breed!),
              FutureBuilder(
                  future:
                  controller.getPetsByCategory(categoryId: category.id.toString()),
                  builder: (context, snapshot) {
                    final response = CloudHelperFunctions.checkMultiRecordState(
                        snapshot: snapshot,
                        loader: const EVerticalProductCardShimmer());
                    if (response != null) return response;
                    final pets = snapshot.data!;

                    return Column(
                      children: [
                        ESectionHeading(
                          title: 'You might like',
                            onPressed: (){},
                        ),
                        EGridProductLayout(
                          itemCount: pets.length,
                          itemBuilder: (_, index) =>
                              EProductCardsVerticalForPet(product: pets[index]),
                        )
                      ],
                    );
                  }),
            ],
          ),
        ),
      ],
    );
  }
}
