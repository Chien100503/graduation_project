import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/common/widgets/texts/product_title_text.dart';
import 'package:pet_shop/features/shop/controllers/products/pet_controller.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/description.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/product_attributes.dart';
import 'package:pet_shop/features/shop/screens/product_details/widget/product_image_slider.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/constants/sizes.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import 'package:pet_shop/common/widgets/texts/section_heading.dart';
import 'package:pet_shop/features/checkout/controller/cart_controller/cart_controller.dart';

import '../../controllers/products/product_controller.dart';

class ProductDetail extends StatefulWidget {
  const ProductDetail({super.key, this.product, this.productId});

  final ProductDetailModel? product;
  final String? productId;

  @override
  State<ProductDetail> createState() => _ProductDetailState();
}

class _ProductDetailState extends State<ProductDetail> {
  late final ProductController controller;
  late final CartController controllerCart;
  ProductDetailModel? currentProduct;

  @override
  void initState() {
    super.initState();
    controller = ProductController.instance;
    controllerCart = CartController.instance;

    if (widget.product != null) {
      currentProduct = widget.product;
    } else if (widget.productId != null) {
      _loadProductDetail();
    }
  }

  Future<void> _loadProductDetail() async {
    final product = await controller.getProductDetail(widget.productId!);
    if (mounted) {
      setState(() => currentProduct = product);
    }
  }

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return Scaffold(
      body: Obx(() {
        if (controller.isLoadingDetail.value && currentProduct == null) {
          return const Center(child: CircularProgressIndicator());
        }

        if (currentProduct == null) {
          return const Center(child: Text('Product not found'));
        }

        return SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              EProductImageSlider(product: currentProduct!),

              Padding(
                padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace, vertical: ESizes.defaultSpace),
                child: Row(
                  children: [
                    Container(
                      height: 22,
                      width: 33,
                      decoration: BoxDecoration(
                        color: dark ? EColors.accent : EColors.thirdColor,
                        borderRadius: BorderRadius.circular(5),
                      ),
                      child: Center(
                        child: Text(
                          '${currentProduct!.percentDiscount.toStringAsFixed(0)} %',
                          style: Theme.of(context).textTheme.labelSmall,
                        ),
                      ),
                    ),
                    const SizedBox(width: ESizes.defaultBetweenItem),
                    Text(
                      '\$${currentProduct!.price}',
                      style: const TextStyle(
                        decoration: TextDecoration.lineThrough,
                        color: Colors.red,
                        fontWeight: FontWeight.w600,
                        fontSize: 16,
                      ),
                    ),
                  ],
                ),
              ),

              Padding(
                padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace),
                child: Text('• ${currentProduct!.name}', style: Theme.of(context).textTheme.bodyLarge),
              ),
              const SizedBox(height: ESizes.defaultBetweenItem),

              Padding(
                padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace, vertical: 4),
                child: Row(
                  children: [
                    Text('Status: ', style: Theme.of(context).textTheme.titleLarge),
                    Text(
                      controller.getProductStockStatus(currentProduct!.stockQuantity),
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                  ],
                ),
              ),

              Padding(
                padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace),
                child: Row(
                  children: [
                    EProductTitleText(title: currentProduct!.brandName),
                    const Icon(Iconsax.verify5, size: 20, color: Colors.blueAccent),
                  ],
                ),
              ),

              EDescription(description: currentProduct!.description ?? ''),

              Divider(thickness: 1, color: dark ? EColors.thirdColor : EColors.primaryColor),
              const SizedBox(height: ESizes.defaultBetweenItem),

              const Padding(
                padding: EdgeInsets.symmetric(horizontal: ESizes.defaultSpace),
                child: ESectionHeading(title: 'Review', showActionButton: false),
              ),
            ],
          ),
        );
      }),
      bottomNavigationBar: currentProduct != null
          ? Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: ElevatedButton.icon(
          icon: const Icon(Iconsax.shopping_bag),
          label: const Text('Thêm vào giỏ hàng'),
          style: ElevatedButton.styleFrom(
            backgroundColor: EColors.primaryColor,
            padding: const EdgeInsets.symmetric(vertical: 16),
          ),
          onPressed: () {
            controllerCart.addProductToCart(currentProduct!);
            Get.snackbar(
              'Thành công',
              'Đã thêm sản phẩm vào giỏ hàng',
              backgroundColor: Colors.green,
              colorText: Colors.white,
              snackPosition: SnackPosition.TOP,
              icon: const Icon(Icons.check_circle, color: Colors.white),
              borderRadius: 8,
              margin: const EdgeInsets.all(16),
              duration: const Duration(seconds: 2),
            );
          },
        ),
      )
          : null,
    );
  }
}
