import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../features/personalizations/controllers/profile/user_controller.dart';
import '../../../features/personalizations/screens/profile/profile.dart';
import '../../../utils/constants/images_strings.dart';
import '../images/circle_images.dart';

class AvatarIcon extends StatelessWidget {
  const AvatarIcon({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(UserController());
    return GestureDetector(
      onTap: () => Get.to(
        () => const Profile(),
        transition: Transition.rightToLeftWithFade,
        duration: const Duration(milliseconds: 400),
      ),
      child: Obx(() {
              final networkImage = controller.profile.value.avatar;
              final image = networkImage.isNotEmpty ? networkImage : EImages.avt;
              return ECircleImage(
                  image: image,
                  boxFit: BoxFit.fill,
                  isNetworkImage: networkImage.isNotEmpty
              );
            }),

    );
  }
}
