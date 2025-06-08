import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../../../common/widgets/images/round_images.dart';
import '../../../../../common/widgets/texts/section_heading.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../controller/checkout_controller.dart';
class BillingPaymentSection extends StatelessWidget {
  const BillingPaymentSection({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(CheckoutController());
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ESectionHeading(
          title: 'Payment method',
          titleButton: 'Change',
          onPressed: () => controller.selectPaymentMethod(context),
        ),
        const SizedBox(height: ESizes.defaultBetweenItem),
        Obx(
          () => Center(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ERoundImages(
                  applyImageRadius: false,
                  boxFit: BoxFit.contain,
                  height: 50,
                  width: 100,
                  imageUrl: controller.selectedPaymentMethod.value.image,
                  bg: Colors.transparent,
                ),
              ],
            ),
          )
        ),
      ],
    );
  }
}
