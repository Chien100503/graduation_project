
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/custom_shape/containers/primary_header_container.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_product.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_pet.dart';
import 'package:pet_shop/features/shop/controllers/products/pet_controller.dart';
import 'package:pet_shop/features/shop/controllers/products/product_controller.dart';
import 'package:pet_shop/features/shop/screens/home/widget/home_categories.dart';
import 'package:pet_shop/utils/constants/colors.dart';

import '../../../../common/widgets/layouts/grid_layout.dart';
import '../../../../common/widgets/shimmer/vertical_product_card_shimmer.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/sizes.dart';
import 'widget/home_appbar.dart';
import 'widget/home_slider.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

Future<void> _refresh(){
  return Future.delayed(const Duration(seconds: 1));
}
  @override
  Widget build(BuildContext context) {
  final controllerProduct = Get.put(ProductController());
  final controllerPet = Get.put(PetController());

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
                          EHomeCategories()
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
                    const ESlider(),
                    const SizedBox(height: ESizes.defaultBetweenItem),
                    // Popular text -view all
                    ESectionHeading(
                      title: 'Popular Pet',
                    onPressed: () => ()),
                    const SizedBox(height: ESizes.defaultBetweenItem),
                    const SizedBox(height: ESizes.defaultBetweenItem),
                    Obx(() {
                      if (controllerPet.isLoading.value) {
                        return const EVerticalProductCardShimmer();
                      }
                      if (controllerPet.allPets.isEmpty) {
                        return const Center(
                          child: Text('Data not found'),
                        );
                      }
                      return EGridProductLayout(
                        itemBuilder: (_, index) => EProductCardsVerticalForPet(
                          product: controllerPet.allPets()[index],
                        ),
                        itemCount:controllerPet.allPets.length,
                      );
                    }),
                    SizedBox(height: ESizes.defaultBetweenSections,),
                    ESectionHeading(
                      title: 'Popular Food, Accessory',
                    onPressed: () => ()),
                    const SizedBox(height: ESizes.defaultBetweenItem),
                    Obx(() {
                      if (controllerProduct.isLoading.value) {
                        return const EVerticalProductCardShimmer();
                      }
                      if (controllerProduct.allProducts.isEmpty) {
                        return const Center(
                          child: Text('Data not found'),
                        );
                      }
                      return EGridProductLayout(
                        itemBuilder: (_, index) => EProductCardsVerticalForProduct(
                          product: controllerProduct.allProducts[index],

                        ),

                        itemCount: min(4, controllerProduct.allProducts.length),
                      );
                    })
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
