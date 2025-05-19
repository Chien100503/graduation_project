import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../features/personalizations/controllers/profile/user_controller.dart';
import '../../../utils/constants/colors.dart';
import '../../../utils/constants/images_strings.dart';
import '../images/circle_images.dart';

class EUserProfile extends StatelessWidget {
  const EUserProfile({super.key, required this.onPressed});

  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(UserController());

    return ListTile(
      leading: Obx(() {
        final avatar = controller.profile.value.avatar ?? '';
        final image = (avatar != null && avatar.isNotEmpty) ? avatar : EImages.avt;
        return ECircleImage(
          height: 60,
          width: 60,
          image: image,
          isNetworkImage: (avatar != null && avatar.isNotEmpty),
        );
      }),
      title: Obx(() => Text(
        controller.profile.value.name,
        style: Theme.of(context)
            .textTheme
            .headlineSmall!
            .apply(color: EColors.primaryColor),
      )),
      subtitle: Obx(() => Text(
        controller.profile.value.email,
        style: Theme.of(context)
            .textTheme
            .bodyMedium!
            .apply(color: EColors.primaryColor),
      )),
      trailing: IconButton(
        icon: const Icon(
          Iconsax.edit,
          color: EColors.primaryColor,
        ),
        onPressed: onPressed,
      ),
    );
  }
}
