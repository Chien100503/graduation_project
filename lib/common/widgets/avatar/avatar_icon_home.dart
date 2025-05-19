import 'package:flutter/material.dart';

import '../../../utils/constants/images_strings.dart';
import '../images/circle_images.dart';

class AvatarIcon extends StatelessWidget {
  const AvatarIcon({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    // final controller = Get.put(UserController());
    return GestureDetector(
      onTap: (){},
      // onTap: () => Get.to(
      //   () => const Profile(),
      //   transition: Transition.rightToLeftWithFade,
      //   duration: const Duration(milliseconds: 400),
      // ),
      // child: Obx(() {
      //       //   // final networkImage = controller.user.value.profilePicture;
      //       //   // final image = networkImage.isNotEmpty ? networkImage : EImages.avt;
      //       //   return ECircleImage(
      //       //       image: EImages.logo,
      //       //       boxFit: BoxFit.fill,
      //       //       // isNetworkImage: networkImage.isNotEmpty
      //       //   );
      //       // }),
        child: ECircleImage(
          image: EImages.logo,
          boxFit: BoxFit.fill,
          // isNetworkImage: networkImage.isNotEmpty
        )
    );
  }
}
