import 'package:get/get.dart';

import '../features/checkout/controller/cart_controller/cart_controller.dart';
import '../features/checkout/controller/checkout_controller.dart';
import '../features/personalizations/controllers/address_controller/address_controller.dart';
import '../utils/helpers/network_manager.dart';




class GeneralBindings extends Bindings{
  @override
  void dependencies() {
    Get.put(NetworkManager());
    Get.put(CartController());
    Get.put(AddressController());
    Get.put(CheckoutController());
  }
}