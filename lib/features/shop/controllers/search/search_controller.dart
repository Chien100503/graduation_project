import 'package:get/get.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';

import '../products/pet_controller.dart';
import '../products/product_controller.dart';

class SearchAllController extends GetxController {
  final searchText = ''.obs;
  final isLoading = false.obs;

  final products = <ProductDetailModel>[].obs;
  final pets = <PetDetailModel>[].obs;

  final ProductController _productController = Get.find<ProductController>();
  final PetController _petController = Get.find<PetController>();


  // Gọi khi text thay đổi
  void onSearchChanged(String value) {
    searchText.value = value.trim().toLowerCase();

    if (searchText.value.isEmpty) {
      products.clear();
      pets.clear();
      return;
    }

    isLoading.value = true;

    Future.delayed(const Duration(milliseconds: 300), () {
      final filteredProducts = _productController.allProducts.where((product) =>
          product.name.toLowerCase().contains(searchText.value)).toList();

      final filteredPets = _petController.allPets.where((pet) =>
          pet.name.toLowerCase().contains(searchText.value)).toList();

      products.assignAll(filteredProducts);
      pets.assignAll(filteredPets);

      isLoading.value = false;
    });
  }
}
