import 'package:get/get.dart';

import '../../../../data/repositories/categories/category_repository.dart';
import '../../models/categories/category_models.dart';

class CategoryController extends GetxController {
  final CategoryRepository _repository = CategoryRepository();

  // Observable variables
  var allCategories = <CategoryModel>[].obs;
  var isLoading = false.obs;
  var selectedType = Rx<String?>(null);
  var searchQuery = ''.obs;

  @override
  void onInit() {
    fetchAllCategories();
    super.onInit();
  }

  List<CategoryModel> get featuredCategories => allCategories;

  // Lấy tất cả categories từ API
  void fetchAllCategories() async {
    try {
      isLoading.value = true;

      final data = await _repository.fetchAllCategories();

      // Convert to List<CategoryModel>
      final List<CategoryModel> fetchedCategories = data
          .map<CategoryModel>((json) => CategoryModel.fromJson(json))
          .toList();

      allCategories.assignAll(fetchedCategories);

      print('Loaded ${allCategories.length} total categories');
    } catch (e) {
      print('Error loading categories: $e');
      Get.snackbar(
        "Error",
        "Failed to load categories",
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Get.theme.colorScheme.error,
        colorText: Get.theme.colorScheme.onError,
      );
    } finally {
      isLoading.value = false;
    }
  }
}