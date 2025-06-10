import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';
import 'package:pet_shop/features/shop/controllers/wish_list/wish_list_controller.dart';

class EFavoriteIconPet extends StatelessWidget {
  const EFavoriteIconPet({super.key, required this.petId});

  final String petId;

  @override
  Widget build(BuildContext context) {
    final controller = WishlistController.instance;
    return Obx(
          () => InkWell(
        onTap: () async => await controller.togglePet(petId),
        child: Container(
          height: 40,
          width: 40,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(100),
              color: Colors.grey.withOpacity(0.3)),
          child: Center(
            child: Icon(
              controller.isFavoritePet(petId) ? Iconsax.heart5 : Iconsax.heart,
              color: controller.isFavoritePet(petId) ? Colors.red : null,
              size: 28,
            ),
          ),
        ),
      ),
    );
  }
}
