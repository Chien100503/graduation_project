
import 'package:flutter/material.dart';
import 'package:pet_shop/common/widgets/custom_shape/containers/primary_header_container.dart';
import 'package:pet_shop/features/shop/home/widget/home_appbar.dart';

import '../../../common/widgets/texts/section_heading.dart';
import '../../../utils/constants/colors.dart';
import '../../../utils/constants/sizes.dart';
import 'widget/home_slider.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

Future<void> _refresh(){
  return Future.delayed(const Duration(seconds: 1));
}
  @override
  Widget build(BuildContext context) {
    // final controller = Get.put(ProductController());

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: _refresh,
        child: SingleChildScrollView(
          child: Column(
            children: [
              const EPrimaryHeaderContainer(
                child: Column(
                  children: [
                    // App bar
                    EHomeAppBar(),
                    SizedBox(height: ESizes.defaultBetweenSections),
                    // SearchBar
                    // ESearchContainer(text: 'Search in Store'),
                    SizedBox(height: ESizes.defaultBetweenSections),
                    // Categories
                    Padding(
                      padding: EdgeInsets.only(left: ESizes.defaultSpace),
                      child: Column(
                        children: [
                          // Heading
                          ESectionHeading(
                            title: 'Popular Categories',
                            showActionButton: false,
                            textColor: EColors.primaryColor,
                          ),
                          SizedBox(height: ESizes.defaultBetweenItem),
                          // Categories
                          // EHomeCategories()
                        ],
                      ),
                    ),
                    SizedBox(height: ESizes.defaultBetweenItem)
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.only(left: 16, right: 16),
                child: Column(
                  children: [
                    // const ESlider(),
                    const SizedBox(height: ESizes.defaultBetweenItem),
                    // Popular text -view all
                    ESectionHeading(
                      title: 'Popular products',
                    onPressed: () => ()),
                    //   onPressed: () => Get.to(
                    //       () => AllProductScreen(
                    //             title: 'Popular Products',
                    //             query: FirebaseFirestore.instance
                    //                 .collection('Products')
                    //                 .where('IsFeatured', isEqualTo: true)
                    //                 .limit(6),
                    //             futureMethod:
                    //                 controller.fetchAllFeaturedProducts(),
                    //           ),
                    //       transition: Transition.fadeIn,
                    //       duration: const Duration(milliseconds: 400)),
                    // ),
                    const SizedBox(height: ESizes.defaultBetweenItem),
                    // Obx(() {
                    //   if (controller.isLoad.value) {
                    //     return const EVerticalProductCardShimmer();
                    //   }
                    //   if (controller.featuredProducts.isEmpty) {
                    //     return const Center(
                    //       child: Text('Data not found'),
                    //     );
                    //   }
                    //   return EGridProductLayout(
                    //     itemBuilder: (_, index) => EProductCardVertical(
                    //       product: controller.featuredProducts[index],
                    //     ),
                    //     itemCount: 6,
                    //   );
                    // })
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
