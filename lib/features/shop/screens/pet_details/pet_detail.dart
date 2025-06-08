import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/common/widgets/chips/chip_color.dart';
import 'package:pet_shop/common/widgets/texts/product_title_text.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/screens/pet_details/widget/pet_image_slider.dart';
import '../../../../common/widgets/custom_shape/circle_container.dart';
import '../../../../common/widgets/texts/section_heading.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';
import '../../../checkout/controller/cart_controller/cart_controller.dart';
import '../../controllers/products/pet_controller.dart';
import '../product_details/widget/description.dart';

class PetDetail extends StatefulWidget {
  const PetDetail({super.key, required this.petId});
  final String petId;

  @override
  State<PetDetail> createState() => _PetDetailState();
}

class _PetDetailState extends State<PetDetail> {
  final petController = PetController.instance;
  PetDetailModel? pet;

  @override
  void initState() {
    super.initState();
    loadPet();
  }

  Future<void> loadPet() async {
    final fetchedPet = await petController.fetchPetById(widget.petId);
    if (mounted) {
      setState(() {
        pet = fetchedPet;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final cartController = CartController.instance;
    final dark = EHelperFunctions.isDarkMode(context);

    if (pet == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    final isColor = EHelperFunctions.getColor(pet!.color) != null;
    final chipColor = isColor ? EHelperFunctions.getColor(pet!.color)! : null;

    return Scaffold(
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: ElevatedButton.icon(
          icon: const Icon(Iconsax.shopping_bag),
          label: const Text('Thêm vào giỏ hàng'),
          style: ElevatedButton.styleFrom(
            backgroundColor: EColors.primaryColor,
            padding: const EdgeInsets.symmetric(vertical: 16),
          ),
          onPressed: () {
            cartController.addPetToCart(pet!);
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
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            EPetImageSlider(product: pet!),
            Padding(
              padding: const EdgeInsets.only(
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
                bottom: ESizes.defaultSpace,
              ),
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
                      child: Text('sale', style: Theme.of(context).textTheme.labelSmall),
                    ),
                  ),
                  const SizedBox(width: ESizes.defaultBetweenItem),
                  Text(
                    '\$${pet!.price}',
                    style: const TextStyle(
                        decoration: TextDecoration.lineThrough,
                        color: Colors.red,
                        fontWeight: FontWeight.w600,
                        fontSize: 16),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace),
              child: EProductTitleText(title: '- Tên thú cưng: ${pet!.name}'),
            ),
            Padding(
              padding: const EdgeInsets.only(
                top: ESizes.defaultSpace,
                left: ESizes.defaultSpace,
                right: ESizes.defaultSpace,
              ),
              child: Row(
                children: [
                  EProductTitleText(title: 'Giống loài: ${pet!.breedName}'),
                ],
              ),
            ),
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: ESizes.defaultSpace, vertical: ESizes.defaultSpace),
              child: ESectionHeading(title: 'Cân nặng', showActionButton: false),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace),
              child: EChipColor(text: '${pet!.size} kg', selected: false, onSelected: (value) {}),
            ),
            const Padding(
              padding: EdgeInsets.only(
                  left: ESizes.defaultSpace, right: ESizes.defaultSpace, top: ESizes.defaultSpace),
              child: ESectionHeading(title: 'Màu', showActionButton: false),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: ESizes.defaultSpace),
              child: ChoiceChip(
                label: isColor ? const SizedBox() : Text(pet!.color),
                selected: false,
                onSelected: (value) {},
                selectedColor: chipColor,
                labelStyle: TextStyle(color: chipColor),
                shape: isColor ? const CircleBorder() : null,
                avatar: isColor
                    ? ECircleContainer(
                    width: 50,
                    height: 50,
                    backgroundColor: chipColor!)
                    : null,
                labelPadding: isColor ? const EdgeInsets.all(0) : null,
                padding: isColor ? const EdgeInsets.all(0) : null,
                backgroundColor: isColor ? chipColor! : (dark ? EColors.accent : EColors.thirdColor),
              ),
            ),
            const SizedBox(height: ESizes.defaultBetweenItem),
            EDescription(description: pet!.description),
            Divider(
                thickness: 1,
                color: dark ? EColors.thirdColor : EColors.primaryColor),
          ],
        ),
      ),
    );
  }
}
