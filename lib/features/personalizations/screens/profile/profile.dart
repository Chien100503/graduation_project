import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/features/personalizations/screens/profile/widgets/update_profile.dart';
import 'package:pet_shop/utils/constants/colors.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../common/widgets/images/circle_images.dart';
import '../../../../common/widgets/loader/loader.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/images_strings.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';
import '../../controllers/profile/user_controller.dart';
import 'widgets/profile_menu.dart';

class Profile extends StatefulWidget {
  const Profile({super.key});

  @override
  _ProfileState createState() => _ProfileState();
}

class _ProfileState extends State<Profile> {
  final controller = UserController.instance;
  IconData _copyIcon = Iconsax.copy;

  void initState() {
    super.initState();
    controller.fetchUserProfile();
  }

  void _copyToClipboard(String text) {
    Clipboard.setData(ClipboardData(text: text));
    setState(() {
      _copyIcon = Icons.check; // Change icon to check
    });
    ECustomSnackBar.showSuccess(title: 'Nice!', message: 'USER ID has been copied to clipboard');
    Future.delayed(const Duration(seconds: 2), () {
      setState(() {
        _copyIcon = Iconsax.copy; // Revert icon back after delay
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    return Scaffold(
      appBar: EAppBar(
        showBackArrow: true,
        title: Text('Change information', style: Theme.of(context).textTheme.titleLarge,),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          await controller.fetchUserProfile();
        },
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(ESizes.defaultSpace),
            child: Column(
              children: [
                Container(
                  height: 170,
                  width: 170,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(100),
                  ),
                  child: Center(
                    child: Stack(
                      children: [
                        Obx(() {
                          final networkImage = controller.profile.value.avatar;
                          final image = networkImage.isNotEmpty ? networkImage : EImages.avt;
                          return ECircleImage(
                              height: 160,
                              width: 160,
                              image: image,
                              isNetworkImage: networkImage.isNotEmpty
                          );
                        }),
                        Positioned(
                          bottom: 0,
                          right: 0,
                          child: Container(
                            height: 40,
                            width: 40,
                            decoration: BoxDecoration(
                                color: dark
                                    ? EColors.thirdColor.withOpacity(0.4)
                                    : EColors.primaryColor.withOpacity(0.4),
                                borderRadius: BorderRadius.circular(100)),
                            child: IconButton(
                              onPressed: () => Get.to(() =>  UpdateProfileScreen()),
                              icon: Icon(
                                Iconsax.edit,
                                color: dark ? Colors.white : Colors.white,
                              ),
                            ),
                          ),
                        )
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: ESizes.defaultBetweenItem),
                Divider(
                  color: dark ? EColors.thirdColor : EColors.primaryColor,
                  thickness: 2,
                  indent: 60,
                  endIndent: 60,
                ),
                const SizedBox(height: ESizes.defaultBetweenItem),
                const ESectionHeading(
                  title: 'Profile information',
                  showActionButton: false,
                ),
                Obx(
                  ()=> EProfileMenu(
                    subName: controller.profile.value.fullName,
                    title: 'Name',
                    onPressed: (){},
                  ),
                ),
                Obx(
                  ()=> EProfileMenu(
                    subName: controller.profile.value.name,
                    title: 'Username',
                    onPressed: () {},
                  ),
                ),
                const SizedBox(height: 10),
                Divider(
                  color: dark ? EColors.thirdColor : EColors.primaryColor,
                  thickness: 2,
                  indent: 60,
                  endIndent: 60,
                ),
                const SizedBox(height: 10),
                const ESectionHeading(
                  title: 'Personal information',
                  showActionButton: false,
                ),
                EProfileMenu(
                  title: 'USER ID',
                  icon: _copyIcon,
                  subName: controller.profile.value.id.toString(),
                  onPressed: () => _copyToClipboard(controller.profile.value.id.toString()),
                ),
                EProfileMenu(
                  title: 'E-mail',
                  subName: controller.profile.value.email,
                  onPressed: () {},
                ),
                Obx(
                  ()=> EProfileMenu(
                    title: 'Phone',
                    subName: controller.profile.value.phone,
                    onPressed: (){},
                  ),
                ),
        
                const SizedBox(height: 10),
                Divider(
                  color: dark ? EColors.thirdColor : EColors.primaryColor,
                  thickness: 2,
                  indent: 60,
                  endIndent: 60,
                ),
                const SizedBox(height: ESizes.defaultBetweenItem,),
                Center(
                  child: TextButton(
                    child: const Text('Close account', style: TextStyle(fontSize: 24, fontWeight: FontWeight.w600, color: Colors.redAccent),),
                    // onPressed: () => controller.deleteAccountWarningPopup()
                    onPressed: (){},
                  ),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
