import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/loader/animation_loader_widget.dart';

import '../../../../../navigation_menu.dart';
import '../../../../../utils/constants/images_strings.dart';
import '../../../../../utils/constants/sizes.dart';

class PaymentSuccess extends StatelessWidget {
  const PaymentSuccess({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            EAnimationLoaderWidget(
              text: 'Thanh toán thành công',
              animation: EImages.successAnimate,
              showAction: true,
              actionText: 'Mong ghé lại lần sau',
              onActionPress: () => Get.to(NavigationMenu()),
            ),
            const SizedBox(
              height: ESizes.defaultBetweenSections,
            ),

          ],
        ),
      ),
    );
  }
}
