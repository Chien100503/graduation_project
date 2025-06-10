import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/layouts/grid_layout.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_pet.dart';
import 'package:pet_shop/common/widgets/products_card/product_cards_vertical_for_product.dart';
import 'package:pet_shop/features/shop/controllers/search/search_controller.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../common/widgets/loader/animation_loader_widget.dart';
import '../../../../utils/constants/images_strings.dart';

class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  final searchInputController = TextEditingController();
  final SearchAllController controller = Get.put(SearchAllController());

  @override
  void initState() {
    super.initState();

    // Đặt debounce để giảm gọi hàm search liên tục
    debounce(controller.searchText, (val) {
      controller.onSearchChanged(val.toString());
    }, time: const Duration(milliseconds: 300));
  }

  @override
  void dispose() {
    searchInputController.dispose();
    Get.delete<SearchController>(); // Dọn dẹp controller khi rời trang
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: EAppBar(
        showBackArrow: true,
        title: TextField(
          controller: searchInputController,
          decoration: const InputDecoration(
            hintText: 'Tìm sản phẩm hoặc thú cưng...',
            border: InputBorder.none,
            prefixIcon: Icon(Icons.search),
          ),
          onChanged: (value) => controller.searchText.value = value,
        ),
      ),
      body: Obx(() {
        if (controller.searchText.isEmpty) {
          return const EAnimationLoaderWidget(
            text: '🔎 Nhập từ khóa để bắt đầu tìm kiếm.',
            animation: EImages.letSearch,
            showAction: true,
            actionText: 'Let\'s fill it',
          );
        }

        if (controller.isLoading.value) {
          return const EAnimationLoaderWidget(
            text: '😿 Không tìm thấy kết quả nào.',
            animation: EImages.notFound,
            showAction: true,
            actionText: 'Let\'s fill it',
          );
        }

        final products = controller.products;
        final pets = controller.pets;

        if (products.isEmpty && pets.isEmpty) {
          return const EAnimationLoaderWidget(
            text: '😿 Không tìm thấy kết quả nào.',
            animation: EImages.notFound,
            showAction: true,
            actionText: 'Let\'s fill it',
          );
        }

        return SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (products.isNotEmpty) ...[
                const Text('📦 Sản phẩm',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 12),
                EGridProductLayout(
                  itemCount: products.length,
                  itemBuilder: (context, index) {
                    final ProductDetailModel product = products[index];
                    return EProductCardsVerticalForProduct(product: product);
                  },
                ),
                const SizedBox(height: 24),
              ],
              if (pets.isNotEmpty) ...[
                const Text('🐶 Thú cưng',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 12),
                EGridProductLayout(

                  itemCount: pets.length,

                  itemBuilder: (context, index) {
                    final PetDetailModel pet = pets[index];
                    return EProductCardsVerticalForPet(product: pet);
                  },
                ),
              ],
            ],
          ),
        );
      }),
    );
  }
}
