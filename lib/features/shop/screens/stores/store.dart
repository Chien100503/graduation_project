import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/category_controller.dart';
import 'package:pet_shop/features/shop/screens/stores/widget/categories_tab_pet.dart';
import 'package:pet_shop/features/shop/screens/stores/widget/categories_tab_product.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../common/widgets/custom_shape/containers/search_container.dart';
import '../../../../common/widgets/tabbar/tabbar.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../checkout/controller/cart_controller/cart_controller.dart';
import '../../../checkout/screen/cart/cart.dart';
import '../../../checkout/screen/cart/widget/cart_menu_icon.dart';
import '../search/search.dart';

class StoreScreen extends StatelessWidget {
  const StoreScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    final categories = CategoryController.instance.featuredCategories;
    return DefaultTabController(
      length: categories.length,
      child: Scaffold(
          appBar:  AppBar(
            title: const Text('Cửa hàng'),
            actions: [
              IconButton(
                icon: const Icon(Icons.search),
                onPressed: () => Get.to(() => SearchPage()),
              ),
              Obx(() => Stack(
                children: [
                  IconButton(
                    icon: const Icon(Icons.shopping_cart),
                    onPressed: () => Get.to(() => CartScreen()),
                  ),
                  if (CartController.instance.noOfCartItems.value > 0)
                    Positioned(
                      right: 6,
                      top: 6,
                      child: Container(
                        padding: const EdgeInsets.all(4),
                        decoration: const BoxDecoration(
                          color: Colors.red,
                          shape: BoxShape.circle,
                        ),
                        child: Text(
                          '${CartController.instance.noOfCartItems.value}',
                          style: const TextStyle(color: Colors.white, fontSize: 12),
                        ),
                      ),
                    ),
                ],
              )),
            ],
          ),
        body: NestedScrollView(
          headerSliverBuilder: (_, innerBoxIsScrolled) {
            return [
              SliverAppBar(
                automaticallyImplyLeading: false,
                pinned: true,
                floating: true,
                backgroundColor: dark ? Colors.black : Colors.white,
                expandedHeight: 440,
                flexibleSpace: FlexibleSpaceBar(
                  background: Padding(
                    padding: const EdgeInsets.all(ESizes.defaultSpace),
                    child: ListView(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      children: [
                        const SizedBox(height: ESizes.defaultBetweenItem),
                        const ESearchContainer(
                          text: 'Search in stores',
                          showBorder: true,
                          showBackground: false,
                          padding: EdgeInsets.zero,
                        ),
                        const SizedBox(height: ESizes.defaultBetweenSections),
                        ESectionHeading(
                          title: 'Featured Brands',
                          onPressed: () =>{}

                              // Get.to(() => const AllBrandScreen(),
                              //     transition: Transition.fadeIn,
                              //     duration: const Duration(milliseconds: 400)),
                        ),
                        const SizedBox(height: ESizes.defaultBetweenItem / 1.5),
                        // Obx(() {
                        //   if (brandController.isLoad.value) return const ShimmerBrand();
                        //   if (brandController.featuredBrands.isEmpty) {
                        //     return const Center(
                        //       child: Text('Data not found!'),
                        //     );
                        //   }
                        //   return EGridProductLayout(
                        //     mainAxisEvent: 70,
                        //     itemCount: brandController.featuredBrands.length,
                        //     itemBuilder: (_, index) {
                        //       final brand = brandController.featuredBrands[index];
                        //       return EBrandCard(
                        //         brand: brand,
                        //         height: 56,
                        //         width: 56,
                        //         showBorder: true,
                        //         onTap: () =>
                        //             Get.to(() => EBrandProductScreen(brand: brand,)),
                        //       );
                        //     },
                        //   );
                        // })
                      ],
                    ),
                  ),
                ),
                bottom: ETabBar(
                  tabs: categories.map((categories) =>
                      Tab(child: Text(categories.name))).toList(),
                ),
              ),
            ];
          },
          body: TabBarView(
            children: categories.map((category) {
              if (category.itemType == 'PET') {
                return ECategoryTabPets(category: category);
              } else if (category.itemType == 'PRODUCT') {
                return ECategoryTabProducts(category: category);
              } else {
                return const Center(child: Text("Unknown category type"));
              }
            }).toList(),
          ),

        ),
      ),
    );
  }
}
