import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/product/pet_repository.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import '../../../../common/widgets/loader/loader.dart';

class PetController extends GetxController {
  static PetController get instance => Get.find();
  final RxList<PetDetailModel> allPets = <PetDetailModel>[].obs;
  final Rx<PetDetailModel?> selectedPet = Rx<PetDetailModel?>(null);
  final PetRepository petRepository = Get.put(PetRepository());
  final RxBool isLoading = false.obs;

  @override
  void onInit() {
    fetchAllPet();
    super.onInit();
  }

  void fetchAllPet() async {
    try {
      isLoading.value = true;
      final pet = await petRepository.getAllPets();
      allPets.assignAll(pet);
    } catch (e) {
      ECustomSnackBar.showError(title: 'Oh Snap!', message: e.toString());
    } finally {
      isLoading.value = false;
    }
  }

  Future<PetDetailModel?> fetchPetById(String petId) async {
    print('=== CONTROLLER: getProductDetail called ===');
    print('Requested Product ID: $petId');

    // 1. Check cache
    final cachedPet = allPets.firstWhereOrNull((pet) => pet.id == petId);

    if (cachedPet != null) {
      print('=== CONTROLLER: Product found in cache ===');
      selectedPet.value = cachedPet;
      return cachedPet;
    }

    try {
      // 2. Fetch from server if not found
      final fetchedPet = await petRepository.getPetById(petId);
      if (fetchedPet != null) {
        selectedPet.value = fetchedPet;
        allPets.add(fetchedPet); // Optional: cache it locally
        print('=== CONTROLLER: Product fetched from server ===');
      }
      return fetchedPet;
    } catch (e) {
      ECustomSnackBar.showError(title: 'Oh Snap!', message: e.toString());
      return null;
    }
  }
}
