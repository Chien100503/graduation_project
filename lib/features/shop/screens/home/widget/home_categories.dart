import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/category_controller.dart';
import 'package:pet_shop/features/shop/models/categories/category_models.dart';

import '../../../../../common/widgets/image_text_widget/vertical_image_text.dart';
import '../../../../../common/widgets/shimmer/shimmer_categories.dart';
import '../../sub_categories/sub_categories.dart';
import '../../sub_categories/sub_pet_category.dart';
import '../../sub_categories/sub_product_category.dart';

class EHomeCategories extends StatelessWidget {
  const EHomeCategories({
    super.key,
    this.product,
  });

  final CategoryModel? product;

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(CategoryController());

    return Obx(() {
      if (controller.isLoading.value) return const EShimmerCategories();

      if (controller.featuredCategories.isEmpty) {
        return Center(
          child: Text(
            'No data found',
            style: Theme.of(context)
                .textTheme
                .bodyMedium!
                .apply(color: Colors.white),
          ),
        );
      }
      return SizedBox(
        height: 100,
        child: ListView.builder(
          shrinkWrap: true,
          scrollDirection: Axis.horizontal,
          itemCount: controller.featuredCategories.length,
          itemBuilder: (_, index) {
            final category = controller.featuredCategories[index];
            print('$category');
            return VerticalImageText(
              title: category.name,
              image: category.image,
              textColor: Colors.black,
              onTap: () {
                if (category.isPet) {
                  Get.to(() => SubPetCategoryScreen(
                      petCategoryId: category.id, category: category,));
                } else if (category.isProduct) {
                  Get.to(() => SubProductCategoryScreen(
                      productCategoryId: category.id, category: category));
                } else {
                  Get.snackbar("Unknown Type",
                      "Unsupported category type: ${category.itemType}");
                }
              },
            );
          },
        ),
      );
    });
  }
}
