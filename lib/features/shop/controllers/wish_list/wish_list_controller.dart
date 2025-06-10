import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';

import '../../../../data/repositories/product/pet_repository.dart';
import '../../../../data/repositories/product/product_repository.dart';

class WishlistController extends GetxController {
  static WishlistController get instance => Get.find();

  var petWishlist = <String>[].obs;
  var productWishlist = <String>[].obs;

  // Map cache pet detail đã load (key là petId)
  final RxMap<String, PetDetailModel> petDetails = <String, PetDetailModel>{}.obs;
  // Map cache product detail đã load (key là productId)
  final RxMap<String, ProductDetailModel> productDetails = <String, ProductDetailModel>{}.obs;

  late PetRepository petRepository;
  late ProductRepository productRepository;

  @override
  void onInit() {
    super.onInit();
    petRepository = Get.find<PetRepository>();
    productRepository = Get.find<ProductRepository>();
    loadWishlist();
  }

  Future<void> loadWishlist() async {
    final prefs = await SharedPreferences.getInstance();
    petWishlist.value = prefs.getStringList('pet_wishlist') ?? [];
    productWishlist.value = prefs.getStringList('product_wishlist') ?? [];

    // Tự động tải chi tiết pet và product theo danh sách wishlist đã load
    for (var petId in petWishlist) {
      await fetchPetDetail(petId);
    }
    for (var productId in productWishlist) {
      await fetchProductDetail(productId);
    }
  }

  bool isFavoritePet(String petId) => petWishlist.contains(petId);
  bool isFavoriteProduct(String productId) => productWishlist.contains(productId);

  Future<void> togglePet(String petId) async {
    final prefs = await SharedPreferences.getInstance();
    if (petWishlist.contains(petId)) {
      petWishlist.remove(petId);
      petDetails.remove(petId);
    } else {
      petWishlist.add(petId);
      await fetchPetDetail(petId);
    }
    await prefs.setStringList('pet_wishlist', petWishlist);
  }

  Future<void> toggleProduct(String productId) async {
    final prefs = await SharedPreferences.getInstance();
    if (productWishlist.contains(productId)) {
      productWishlist.remove(productId);
      productDetails.remove(productId);
    } else {
      productWishlist.add(productId);
      await fetchProductDetail(productId);
    }
    await prefs.setStringList('product_wishlist', productWishlist);
  }

  // Lấy chi tiết pet theo petId (cache hoặc gọi API)
  Future<PetDetailModel?> fetchPetDetail(String petId) async {
    if (petDetails.containsKey(petId)) {
      return petDetails[petId];
    }
    try {
      final petDetail = await petRepository.getPetById(petId);
      petDetails[petId] = petDetail;
      return petDetail;
    } catch (e) {
      print('Lỗi lấy chi tiết pet: $e');
      return null;
    }
  }

  Future<ProductDetailModel?> fetchProductDetail(String productId) async {
    if (productDetails.containsKey(productId)) {
      return productDetails[productId];
    }
    try {
      final productDetail = await productRepository.getProductById(productId);
      productDetails[productId] = productDetail;
      return productDetail;
    } catch (e) {
      print('Lỗi lấy chi tiết product: $e');
      return null;
    }
  }
}
