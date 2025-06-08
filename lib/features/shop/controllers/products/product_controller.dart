import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/product/product_repository.dart';
import '../../../../common/widgets/loader/loader.dart';
import '../../models/products/product_detail_model.dart';

class ProductController extends GetxController {
  static ProductController get instance => Get.find();
  final RxList<ProductDetailModel> allProducts = <ProductDetailModel>[].obs;
  final ProductRepository productRepository = Get.put(ProductRepository());
  final RxBool isLoading = false.obs;

  @override
  void onInit() {
    fetchAllProducts();
    super.onInit();
  }

  void fetchAllProducts() async {
    try {
      isLoading.value = true;
      final products = await productRepository.getAllProducts();
      allProducts.assignAll(products); // Gán toàn bộ sản phẩm
    } catch (e) {
      ECustomSnackBar.showError(title: 'Oh Snap!', message: e.toString());
    } finally {
      isLoading.value = false;
    }
  }

  String getProductStockStatus(int stockQuantity) {
    return stockQuantity > 0 ? 'In stock' : 'Out of stock';
  }
}
