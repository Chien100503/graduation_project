import 'package:get/get.dart';
import 'package:pet_shop/features/shop/controllers/products/pet_controller.dart';
import 'package:pet_shop/features/shop/controllers/products/product_controller.dart';

import '../features/checkout/controller/cart_controller/cart_controller.dart';
import '../features/checkout/controller/checkout_controller.dart';
import '../features/shop/controllers/wish_list/wish_list_controller.dart';
import '../utils/helpers/network_manager.dart';




class GeneralBindings extends Bindings{
  @override
  void dependencies() {
    Get.put(NetworkManager());
    Get.put(ProductController());
    Get.put(PetController());
    Get.put(CartController());
    Get.put(WishlistController());
    Get.put(CheckoutController());
  }
}