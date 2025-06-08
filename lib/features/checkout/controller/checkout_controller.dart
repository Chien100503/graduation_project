import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/loader/loader.dart';
import 'package:pet_shop/features/checkout/screen/payment/widget/payment_qrCode.dart';
import 'package:pet_shop/features/checkout/screen/payment/widget/payment_success.dart';

import '../../../common/widgets/texts/section_heading.dart';
import '../../../data/repositories/payment/payment_repository.dart';
import '../../../utils/constants/images_strings.dart';
import '../../../utils/constants/sizes.dart';
import '../models/payment_method_model.dart';
import '../screen/payment/widget/payment_title.dart';


class CheckoutController extends GetxController {
  static CheckoutController get instance => Get.find();

  final Rx<PaymentMethodModel> selectedPaymentMethod = PaymentMethodModel.empty().obs;
  final PaymentRepository _paymentRepository = PaymentRepository();

  @override
  void onInit() {
    selectedPaymentMethod.value = PaymentMethodModel(name: 'PAYOS', image: EImages.payos);
    super.onInit();
  }

  Future<dynamic> selectPaymentMethod(BuildContext context) {
    return showModalBottomSheet(context: context, builder: (_) => SingleChildScrollView(
      child: Container(
        padding: const EdgeInsets.all(ESizes.lg),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const ESectionHeading(title: 'Select Payment Method', showActionButton: false,),
            const SizedBox(height: ESizes.defaultBetweenSections),
            EPaymentTitle(paymentMethod: PaymentMethodModel(name: 'PAYOS', image: EImages.payos)),
            const SizedBox(height: ESizes.defaultBetweenItem / 2),
            EPaymentTitle(paymentMethod: PaymentMethodModel(name: 'COD', image: EImages.COD)),
            const SizedBox(height: ESizes.defaultBetweenItem / 2),
            const SizedBox(height: ESizes.defaultBetweenSections),
          ],
        ),
      ),
    ));
  }
  Future<void> createOrder({required int? addressId, required String paymentMethod}) async {
    if (addressId == null) {
      Get.snackbar('Lỗi', 'Vui lòng chọn địa chỉ giao hàng.');
      return;
    }

    try {
      final response = await _paymentRepository.createOrder(
        addressId: addressId,
        paymentMethod: paymentMethod,
      );
      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}'); // ✅ In toàn bộ response

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (paymentMethod == 'PAYOS') {
          Get.to(() => PaymentQRCodeScreen(
            qrData: data['qrCode'],
            checkoutUrl: data['checkoutUrl'],
          ));
        } else {
          Get.to(() => const PaymentSuccess());
        }
      } else {
        ECustomSnackBar.showError(title: 'Xin lỗi', message: 'Không thể mua đơn hàng');

      }
    } catch (e) {
      Get.snackbar('Exception', e.toString());
    }
  }
}