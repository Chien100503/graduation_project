
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:carousel_slider/carousel_slider.dart';
import '../../../../../common/widgets/images/banner_image.dart';
import '../../../../../common/widgets/shimmer/shimmer.dart';
import '../../../../../utils/constants/colors.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../controllers/banner_controller.dart';



class ESlider extends StatelessWidget {
  const ESlider({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(BannerController());
    return Obx(
      () {
        if (controller.isLoad.value) return const EShimmerEffect(width: double.infinity, height: 190, radius: 15);

        if (controller.banners.isEmpty) {
          return const Center(child: Text('No data found!'));
        } else {
          return Column(
            children: [
              CarouselSlider(
                  options: CarouselOptions(
                      onPageChanged: (index, _) =>
                          controller.updatePageIndicator(index),
                      viewportFraction: 1,
                      autoPlay: true,
                      autoPlayAnimationDuration: const Duration(seconds: 2),
                      enlargeCenterPage: true,
                      autoPlayCurve: Curves.fastEaseInToSlowEaseOut),
                  items: controller.banners
                      .map((banner) => EBannerImage(
                        imageUrl: banner.image,
                        isNetworkImage: true,
                        onPress: () {},
                      )).toList()),

              const SizedBox(height: ESizes.defaultBetweenItem),
              Obx(
                    () =>
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        for (int i = 0; i < controller.banners.length; i++)
                          Container(
                            margin: const EdgeInsets.only(right: 5),
                            height: 7,
                            width: 25,
                            decoration: BoxDecoration(
                              color: controller.carousalCurrentIndex.value == i
                                  ? EColors.accent
                                  : Colors.grey,
                              borderRadius: BorderRadius.circular(100),
                            ),
                          ),
                      ],
                    ),
              )
            ],
          );
        }
      }
    );
  }
}
