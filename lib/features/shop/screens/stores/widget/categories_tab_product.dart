import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_pet.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_product.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/category_controller.dart';

import '../../../../../common/widgets/layouts/grid_layout.dart';
import '../../../../../common/widgets/shimmer/vertical_product_card_shimmer.dart';
import '../../../../../common/widgets/texts/section_heading.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../../utils/helpers/cloud_helper_functions.dart';
import '../../../models/categories/category_models.dart';

class ECategoryTabProducts extends StatelessWidget {
  const ECategoryTabProducts({super.key, required this.category});

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

              FutureBuilder(
                  future:
                  controller.getProductsByCategory(categoryId: category.id.toString()),
                  builder: (context, snapshot) {
                    final response = CloudHelperFunctions.checkMultiRecordState(
                        snapshot: snapshot,
                        loader: const EVerticalProductCardShimmer());
                    if (response != null) return response;
                    final products = snapshot.data!;

                    return Column(
                      children: [
                        ESectionHeading(
                          title: 'You might like',
                            onPressed: (){},
                        ),
                        EGridProductLayout(
                          itemCount: products.length,
                          itemBuilder: (_, index) =>
                              EProductCardsVerticalForProduct(product: products[index],),
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
