import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/categories/category_pet_repository.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import '../../models/categories/breed_model.dart';
class PetCategoryController extends GetxController {
  final CategoryPetRepository _repository = CategoryPetRepository();
  var isLoading = false.obs;

  Future<List<BreedModel>> getBreeds(int categoryId) async {
    try {
      isLoading.value = true;
      final breeds = await _repository.fetchBreedsByCategory(categoryId);
      return breeds;
    } finally {
      isLoading.value = false;
    }
  }

  Future<List<PetDetailModel>> getPetsByBreed(int categoryId, int breedId) async {
    try {
      isLoading.value = true;
      final pets = await _repository.fetchPetsByBreed(categoryId, breedId);
      return pets;
    } finally {
      isLoading.value = false;
    }
  }

  void clearCache() {
    _repository.clearCache();
  }
}
