import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/features/authen/screens/login/login.dart';

import '../../../../common/widgets/custom_shape/containers/primary_header_container.dart';
import '../../../../common/widgets/list_title/setting_menu_title.dart';
import '../../../../common/widgets/list_title/user_profile.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../data/repositories/user_repository.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';
import '../../../personalizations/screens/profile/profile.dart';


class Setting extends StatelessWidget {
  const Setting({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Header
            EPrimaryHeaderContainer(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  EAppBar(
                    title: Text(
                      'Account',
                      style: Theme.of(context)
                          .textTheme
                          .headlineMedium!
                          .apply(color: EColors.primaryColor),
                    ),
                  ),
                  const SizedBox(height: ESizes.defaultBetweenItem),
                  EUserProfile(
                    onPressed: () => Get.to(
                      () => const Profile(),
                      transition: Transition.cupertinoDialog,
                      duration: const Duration(milliseconds: 500),
                    ),
                  ),
                  const SizedBox(height: 30),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(ESizes.defaultSpace),
              child: Column(
                children: [
                  const ESectionHeading(
                    showActionButton: false,
                    title: 'Account setting',
                  ),
                  const SizedBox(height: ESizes.defaultBetweenItem),
                  // ESettingMenuTitle(
                  //   onTap: () => Get.to(() => const AddressScreen(),
                  //       transition: Transition.downToUp,
                  //       duration: const Duration(milliseconds: 400)),
                  //   title: 'My address',
                  //   subTitle: 'Set shopping delivery address',
                  //   icon: Icon(
                  //     Iconsax.location,
                  //     size: 30,
                  //     color: dark ? EColors.thirdColor : EColors.primaryColor,
                  //   ),
                  // ),
                  const Divider(),
                  // ESettingMenuTitle(
                  //   onTap: () => Get.to(() => const OrderScreen(),
                  //       transition: Transition.downToUp,
                  //       duration: const Duration(milliseconds: 400)),
                  //   title: 'My Orders',
                  //   subTitle: 'Set shopping delivery address',
                  //   icon: Icon(
                  //     Iconsax.smart_car4,
                  //     size: 30,
                  //     color: dark ? EColors.thirdColor : EColors.primaryColor,
                  //   ),
                  // ),
                  const Divider(),
                  // ESettingMenuTitle(
                  //   onTap: () => Get.to(() => const CartScreen(),
                  //       transition: Transition.downToUp,
                  //       duration: const Duration(milliseconds: 400)),
                  //   title: 'My cart',
                  //   subTitle: 'Withdraw balance to registered bank account',
                  //   icon: Icon(
                  //     Iconsax.shopping_cart,
                  //     size: 30,
                  //     color: dark ? EColors.thirdColor : EColors.primaryColor,
                  //   ),
                  // ),
                  const Divider(),
                  ESettingMenuTitle(
                    onTap: () {},
                    title: 'Bank account',
                    subTitle: 'Withdraw balance to registered bank account',
                    icon: Icon(
                      Iconsax.copy,
                      size: 30,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                  ),
                  const Divider(),
                  // ESettingMenuTitle(
                  //   onTap: () => Get.to(() => DemoPaymentStripe()),
                  //   title: 'Notification',
                  //   subTitle: 'Set any kind of notification message',
                  //   icon: Icon(
                  //     Iconsax.notification,
                  //     size: 30,
                  //     color: dark ? EColors.thirdColor : EColors.primaryColor,
                  //   ),
                  // ),
                  const Divider(),
                  ESettingMenuTitle(
                    onTap: () {},
                    title: 'Account security',
                    subTitle:
                        'E-Wallet, credit cards & instant debit registered',
                    icon: Icon(
                      Iconsax.safe_home,
                      size: 30,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                  ),
                  const Divider(),
                  const ESectionHeading(
                    title: 'App setting',
                    showActionButton: false,
                  ),
                  const SizedBox(height: ESizes.defaultBetweenItem),
                  ESettingMenuTitle(
                    onTap: () {},
                    title: 'Load data',
                    subTitle: 'Upload data to your Cloud Firebase',
                    icon: Icon(
                      Iconsax.document_upload,
                      size: 30,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                  ),
                  const SizedBox(height: ESizes.defaultBetweenSections * 2),

                  // Logout
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: () async {
                        try {
                          await UserRepository().logout();
                          Get.offAll(const LoginScreen());
                        } catch (e) {
                          Get.snackbar('Lỗi', 'Đăng xuất thất bại: $e');
                        }
                      },
                      child: const Text(
                        'Logout',
                        style: TextStyle(
                          color: EColors.thirdColor,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
