import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../../common/widgets/appbar/appbar.dart';
import '../../../../../utils/constants/colors.dart';
import '../../../../common/widgets/avatar/avatar_icon_home.dart';
import '../../../../utils/constants/images_strings.dart';

class EHomeAppBar extends StatelessWidget {
  const EHomeAppBar({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return const EAppBar(
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: EdgeInsets.only(top: 20, left: 0),
            child: Image(image: AssetImage(EImages.logo), height: 100),
          ),
        ],
      ),
      actions: [
        // Avatar icon
        const AvatarIcon(),
        // CartCounterIcon(
        //   icons: const Icon(Iconsax.shopping_bag),
        //   onPressed: () => Get.to(
        //     () => const CartScreen(),
        //     transition: Transition.rightToLeftWithFade,
        //     duration: const Duration(milliseconds: 400),
        //   ),
        //   iconColor: EColors.primaryColor,
        // ),
      ],
    );
  }
}
