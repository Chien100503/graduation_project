import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/pet_category_controller.dart';
import 'package:pet_shop/features/shop/models/categories/category_models.dart';
import 'package:pet_shop/features/shop/models/categories/breed_model.dart';
import 'package:pet_shop/features/shop/models/products/pet_model.dart';

import '../../../../common/widgets/products_card/product_cards_horizontal.dart';

class SubPetCategoryScreen extends StatelessWidget {
  final int petCategoryId;
  final CategoryModel category;

  const SubPetCategoryScreen({
    super.key,
    required this.category,
    required this.petCategoryId,
  });

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(PetCategoryController());

    return Scaffold(
      appBar: EAppBar(
        title: Text(
          category.name,
          style: Theme.of(context).textTheme.headlineMedium,
        ),
        showBackArrow: true,
      ),
      body: FutureBuilder<List<BreedModel>>(
        future: controller.fetchBreedsByCategory(petCategoryId),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            print('Lỗi khi tải giống thú cưng: ${snapshot.error}');
            return const Center(child: Text("Lỗi khi tải giống thú cưng"));
          }

          if (!snapshot.hasData || snapshot.data!.isEmpty) {
            print('Không có giống thú cưng nào');
            return const Center(child: Text("Không có giống thú cưng nào."));
          }

          final breeds = snapshot.data!;
          print('Breeds: ${breeds.map((e) => e.name).toList()}');

          return ListView.builder(
            itemCount: breeds.length,
            itemBuilder: (context, i) {
              final breed = breeds[i];

              return FutureBuilder<List<PetModel>>(
                future: controller.fetchPetsByBreed(petCategoryId, breed.id),
                builder: (context, petSnapshot) {
                  if (petSnapshot.connectionState == ConnectionState.waiting) {
                    return const Padding(
                      padding: EdgeInsets.symmetric(vertical: 20),
                      child: Center(child: CircularProgressIndicator()),
                    );
                  }

                  if (petSnapshot.hasError) {
                    print('Lỗi khi tải pets của giống ${breed.name}: ${petSnapshot.error}');
                    return const SizedBox();
                  }

                  if (!petSnapshot.hasData || petSnapshot.data!.isEmpty) {
                    print('Không có pet nào cho giống ${breed.name}');
                    return const SizedBox();
                  }

                  final pets = petSnapshot.data!;
                  print('Pets for ${breed.name}: ${pets.map((e) => e.name).toList()}');

                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Text(
                          breed.name,
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                      ),
                      Column(
                        children: pets.map((pet) => EProductCardsHorizontal(product: pet)).toList(),
                      ),
                    ],
                  );
                },
              );
            },
          );
        },
      ),
    );
  }
}
