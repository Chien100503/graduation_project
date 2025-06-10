import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/product/product_repository.dart';
import 'package:pet_shop/features/shop/models/categories/breed_model.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/pet_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_model.dart';
import '../../../../data/repositories/categories/category_repository.dart';
import '../../models/categories/category_models.dart';

class CategoryController extends GetxController {
  static CategoryController get instance => Get.find();

  final CategoryRepository _repository = CategoryRepository();

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

  void fetchAllCategories() async {
    try {
      isLoading.value = true;

      final data = await _repository.fetchAllCategories();

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
  Future<List<PetDetailModel>> getPetsByCategory({required String categoryId}) async {
    try {
      final pets = await _repository.getAllPetsByCategory(categoryId);
      return pets;
    } catch (e) {
      print('Lỗi khi lấy thú cưng theo category: $e');
      return [];
    }
  }
  Future<List<ProductDetailModel>> getProductsByCategory({required String categoryId}) async {
    try {
      final products = await _repository.getAllProductsByCategory(categoryId);
      return products;
    } catch (e) {
      print('Lỗi khi lấy sản phẩm theo category: $e');
      return [];
    }
  }

  Future<List<BreedModel>> getAllBreedById({required String breedId}) async {
    try {
      final breeds = await _repository.getAllBreedById(breedId);
      return breeds;
    } catch (e){
      print('Lỗi khi lấy Breed theo Id: $e');
      return[];

    }
  }

}
