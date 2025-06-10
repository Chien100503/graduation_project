import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/categories/category_repository.dart';

import '../../models/categories/brand_model.dart';

class BrandController extends GetxController {
  static BrandController get instance => Get.find();

  final CategoryRepository _repository = CategoryRepository();

  var featuredBrands = <BrandModel>[].obs;
  var isLoading = false.obs;

  @override
  void onInit() {
    super.onInit();
    fetchFeaturedBrands();
  }

  Future<void> fetchFeaturedBrands() async {
    try {
      isLoading.value = true;

      final data = await _repository.getAllBrand();
      featuredBrands.assignAll(data);
    } catch (e) {
      print('Lỗi khi tải danh sách thương hiệu: $e');
      // Optionally: Hiển thị snackbar báo lỗi
      // Get.snackbar('Lỗi', 'Không thể tải danh sách thương hiệu');
    } finally {
      isLoading.value = false;
    }
  }
}
