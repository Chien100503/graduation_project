import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/layouts/grid_layout.dart';
import 'package:pet_shop/common/widgets/products_card/product_card_vertical_for_pet_wishlist.dart';
import 'package:pet_shop/common/widgets/products_card/product_card_vertical_for_product_wishlist.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_pet.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_product.dart';
import 'package:pet_shop/features/shop/controllers/wish_list/wish_list_controller.dart';
import 'package:pet_shop/utils/constants/sizes.dart';

class WishListScreen extends StatelessWidget {
  const WishListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = WishlistController.instance;

    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Wishlist'),
          bottom: const TabBar(
            tabs: [
              Tab(icon: Icon(Icons.shopping_bag), text: "Sản phẩm"),
              Tab(icon: Icon(Icons.pets), text: "Thú cưng"),
            ],
          ),
        ),
        body: Obx(() {
          final petIds = controller.petWishlist;
          final productIds = controller.productWishlist;

          return TabBarView(
            children: [
              productIds.isEmpty
                  ? const Center(child: Text("Chưa có sản phẩm yêu thích"))
                  : Padding(
                      padding: const EdgeInsets.all(ESizes.defaultSpace),
                      child: EGridProductLayout(
                        itemCount: productIds.length,
                        itemBuilder: (_, index) {
                          final id = productIds[index];

                          final product = controller.productDetails[id];
                          print(
                              "Ảnh sản phẩm [${controller.productDetails[id]?.imageUrl}]");
                          if (product == null) {
                            controller.fetchProductDetail(id);
                            return const ListTile(
                              leading: CircularProgressIndicator(),
                              title: Text('Đang tải sản phẩm...'),
                            );
                          }
                          return EProductCardsVerticalForProductWishlist(
                              product: product);
                        },
                      ),
                    ),
              petIds.isEmpty
                  ? const Center(child: Text("Chưa có thú cưng yêu thích"))
                  : Padding(
                      padding: const EdgeInsets.all(ESizes.defaultSpace),
                      child: EGridProductLayout(
                        itemCount: petIds.length,
                        itemBuilder: (_, index) {
                          final id = petIds[index];

                          final pet = controller.petDetails[id];
                          print(
                              "Ảnh sản phẩmm ${pet?.percentDiscount.toString()}");
                          if (pet == null) {
                            controller.fetchPetDetail(id);

                            return const ListTile(
                              leading: CircularProgressIndicator(),
                              title: Text('Đang tải thú cưng...'),
                            );
                          }
                          return EProductCardsVerticalForPetWishlist(
                              product: pet);
                        },
                      ),
                    ),
            ],
          );
        }),
      ),
    );
  }
}
