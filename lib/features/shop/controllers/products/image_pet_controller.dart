import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/screens/product_details/product_detail.dart';

import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';

class ImagesPetController extends GetxController {
  static ImagesPetController get instance => Get.find();

  // Variable
  RxString selectedProductImages = ''.obs;

  // Get all Images from product and variation
  List<String> getAllProductImages(PetDetailModel product) {
    Set<String> images = {};

    // load thumbnail image
    images.add(product.imageUrls.first);
    // assign thumbnail as selected image
    selectedProductImages.value = product.imageUrls.first;

    // get all images from the ProductModel if not null
    if (product.imageUrls != null) {
      images.addAll(product.imageUrls!);
    }

    return images.toList();
  }

  // show images popup
  void showEnlargedImage(List<String> images, int initialIndex) {
    final TransformationController controller = TransformationController();
    TapDownDetails? doubleTapDetails;
    bool isZoomed = false;
    final PageController pageController = PageController(initialPage: initialIndex);
    Get.to(
          () => Scaffold(
        backgroundColor: EColors.accent,
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Expanded(
              child: GestureDetector(
                onDoubleTapDown: (details) {
                  doubleTapDetails = details;
                },
                onDoubleTap: () {
                  final double scale = isZoomed ? 1 : 3;
                  final position = doubleTapDetails!.localPosition;
                  final double x = -position.dx * (scale - 1);
                  final double y = -position.dy * (scale - 1);
                  controller.value = Matrix4.identity()
                    ..translate(x, y)
                    ..scale(scale);
                  isZoomed = !isZoomed;
                },
                child: PageView.builder(
                  controller: pageController,
                  itemCount: images.length,
                  itemBuilder: (_, index) {
                    return InteractiveViewer(
                      transformationController: controller,
                      child: CachedNetworkImage(
                        imageUrl: images[index],
                        fit: BoxFit.contain,
                      ),
                    );
                  },
                ),
              ),
            ),
            const SizedBox(height: ESizes.defaultBetweenItem),
            Align(
              alignment: Alignment.bottomCenter,
              child: SizedBox(
                width: 200,
                child: ElevatedButton(
                  onPressed: () => Get.back(),
                  child: const Text('Close'),
                ),
              ),
            ),
            const SizedBox(height: ESizes.defaultBetweenItem),
          ],
        ),
      ),
    );
  }
}
