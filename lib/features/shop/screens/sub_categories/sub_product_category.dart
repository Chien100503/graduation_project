import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/product_category_controller.dart';

import '../../../../common/widgets/images/round_images.dart';
import '../../../../common/widgets/products_card/product_cards_horizontal_for_product.dart';
import '../../../../common/widgets/shimmer/horizontal_product_shimmer.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/images_strings.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/cloud_helper_functions.dart';
import '../../models/categories/category_models.dart';

class SubProductCategoryScreen extends StatelessWidget {
  final int productCategoryId;
  final CategoryModel category;

  const SubProductCategoryScreen(
      {super.key, required this.category, required this.productCategoryId});

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(ProductCategoryController());

    return Scaffold(
      appBar: EAppBar(title: Text(category.name, style: Theme.of(context).textTheme.headlineMedium,), showBackArrow: true,),
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
                future: controller.getTypesByProductCategoryId(productCategoryId),
                builder: (context, snapshot) {
                  const loader = EHorizontalProductShimmer();
                  final widget = CloudHelperFunctions.checkMultiRecordState(
                      snapshot: snapshot, loader: loader);
                  if (widget != null) return widget;

                  final types = snapshot.data!;
                  return ListView.builder(
                    shrinkWrap: true,
                    itemCount: types.length,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (_, index) {
                      final type = types[index];

                      return FutureBuilder(
                        future: controller.fetchProductByType(productCategoryId, type.id),
                        builder: (context, snapshot) {
                          final widget = CloudHelperFunctions.checkMultiRecordState(
                              snapshot: snapshot, loader: loader);
                          if (widget != null) return widget;
                          final types = snapshot.data!;
                          return Column(
                            children: [
                              ESectionHeading(
                                title: '${type.name}',
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
                                  itemCount: types.length,
                                  itemBuilder: (context, index) =>
                                      EProductCardsHorizontalForProduct(product: types[index]),
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
