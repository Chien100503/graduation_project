import 'package:get/get.dart';

import '../features/checkout/controller/cart_controller/cart_controller.dart';
import '../utils/helpers/network_manager.dart';




class GeneralBindings extends Bindings{
  @override
  void dependencies() {
    // TODO: implement dependencies
    Get.put(NetworkManager());
    Get.put(CartController());
    // Get.put(VariationController());
    // Get.put(AddressController());
    // Get.put(CheckoutController());
  }
}