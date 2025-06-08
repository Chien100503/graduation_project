import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';

import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';

class ImagesProductController extends GetxController {
  static ImagesProductController get instance => Get.find();

  RxString selectedProductImages = ''.obs;

  // Lấy tất cả hình ảnh của sản phẩm
  List<String> getAllProductImages(ProductDetailModel product) {
    Set<String> images = {};

    // Nếu imageUrls không rỗng, lấy ảnh đầu tiên; nếu không, dùng thumbnailUrl
    if (product.imageUrl.isNotEmpty) {
      images.add(product.imageUrl.first);
      selectedProductImages.value = product.imageUrl.first;
      images.addAll(product.imageUrl);
    } else {
      images.add(product.thumbnailUrl);
      selectedProductImages.value = product.thumbnailUrl;
    }

    return images.toList();
  }

  // Hiển thị popup phóng to ảnh
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
            SizedBox(
              width: 200,
              child: ElevatedButton(
                onPressed: () => Get.back(),
                child: const Text('Đóng'),
              ),
            ),
            const SizedBox(height: ESizes.defaultBetweenItem),
          ],
        ),
      ),
    );
  }
}
