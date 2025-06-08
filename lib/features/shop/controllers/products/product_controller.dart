import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/product/product_repository.dart';
import '../../../../common/widgets/loader/loader.dart';
import '../../models/products/product_detail_model.dart';

class ProductController extends GetxController {
  static ProductController get instance => Get.find();
  final RxList<ProductDetailModel> allProducts = <ProductDetailModel>[].obs;
  final Rx<ProductDetailModel?> selectedProduct = Rx<ProductDetailModel?>(null);
  final ProductRepository productRepository = Get.put(ProductRepository());
  final RxBool isLoading = false.obs;
  final RxBool isLoadingDetail = false.obs;

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

  // Thêm method mới để lấy chi tiết sản phẩm
  Future<void> fetchProductById(String productId) async {
    try {
      print('=== CONTROLLER: Starting fetchProductById ===');
      print('Product ID: $productId');

      isLoadingDetail.value = true;
      print('Loading state set to true');

      final product = await productRepository.getProductById(productId);

      print('=== CONTROLLER: Product fetched successfully ===');
      print('Product received: ${product.name}');

      selectedProduct.value = product;
      print('Selected product updated in controller');

    } catch (e) {
      print('=== CONTROLLER: Error in fetchProductById ===');
      print('Error: $e');
      ECustomSnackBar.showError(title: 'Error!', message: e.toString());
    } finally {
      isLoadingDetail.value = false;
      print('Loading state set to false');
      print('=== CONTROLLER: fetchProductById completed ===');
    }
  }

  // Method để lấy sản phẩm từ cache hoặc fetch từ server
  Future<ProductDetailModel?> getProductDetail(String productId) async {
    print('=== CONTROLLER: getProductDetail called ===');
    print('Requested Product ID: $productId');

    // Kiểm tra xem sản phẩm đã có trong allProducts chưa
    final cachedProduct = allProducts.firstWhereOrNull((product) => product.id == productId);

    if (cachedProduct != null) {
      print('=== CONTROLLER: Product found in cache ===');
      print('Cached product: ${cachedProduct.name}');
      selectedProduct.value = cachedProduct;
      return cachedProduct;
    }

    print('=== CONTROLLER: Product not in cache, fetching from server ===');
    // Nếu không có trong cache, fetch từ server
    await fetchProductById(productId);

    print('=== CONTROLLER: Returning selected product ===');
    print('Final product: ${selectedProduct.value?.name ?? 'null'}');
    return selectedProduct.value;
  }

  String getProductStockStatus(int stockQuantity) {
    return stockQuantity > 0 ? 'In stock' : 'Out of stock';
  }
}