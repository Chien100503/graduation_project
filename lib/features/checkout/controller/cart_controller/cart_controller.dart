

import 'package:get/get.dart';
import 'package:get/get_state_manager/src/simple/get_controllers.dart';
import 'package:pet_shop/features/checkout/models/cart_item_model.dart';

import '../../../../data/repositories/cart/cart_repository.dart';
import '../../../shop/models/products/pet_detail_model.dart';
import '../../../shop/models/products/product_detail_model.dart';
import '../../models/cart_model.dart';

class CartController extends GetxController {
  static CartController get instance => Get.find();
  final CartRepository _cartRepo = CartRepository();
  final RxList<CartItemModel> cartItems = <CartItemModel>[].obs;

  var totalPrice = 0.0.obs;


  /// ✅ Hàm thêm sản phẩm từ API
  void addProductToCart(ProductDetailModel product) async {
    await CartRepository().addToCart(
      productId: product.id,
      price: product.price.toDouble(),
      quantity: 1,
    );
  }

  /// ✅ Hàm thêm thú cưng từ API
  void addPetToCart(PetDetailModel pet) async {
    await CartRepository().addToCart(
      petId: pet.id,
      price: pet.price.toDouble(),
      quantity: 1,
    );
  }

  void removeFromCart(int id) {
    cartItems.removeWhere((item) => item.id == id);
  }

  void clearCart() {
    cartItems.clear();
    totalPrice.value = 0.0;
  }

  CartModel getCartModel() {
    return CartModel(
      id: 1,
      items: cartItems,
      totalPrice: totalPrice.value,
    );
  }

  //
  Future<void> fetchCartItems() async {
    try {
      final items = await _cartRepo.getCartItems();
      cartItems.assignAll(items);
    } catch (e) {
      print('Lỗi khi lấy giỏ hàng: $e');
    }
  }
  void updateItemQuantity(int id, int newQuantity) {
    final index = cartItems.indexWhere((item) => item.id == id);
    if (index != -1) {
      if (newQuantity <= 0) {
        cartItems.removeAt(index);
      } else {
        final updatedItem = cartItems[index].copyWith(quantity: newQuantity);
        cartItems[index] = updatedItem;
      }
    }
  }
}
