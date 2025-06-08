import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/product/pet_repository.dart';
import 'package:pet_shop/data/repositories/product/product_repository.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import '../../../../common/widgets/loader/loader.dart';
import '../../models/products/product_detail_model.dart';

class PetController extends GetxController {
  static PetController get instance => Get.find();
  final RxList<PetDetailModel> allPets = <PetDetailModel>[].obs;
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
}
