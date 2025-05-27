import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/features/shop/controllers/categories_controller/product_category_controller.dart';
import 'package:pinput/pinput.dart';

import '../../models/categories/category_models.dart';

class SubProductCategoryScreen extends StatelessWidget {
  final int productCategoryId;
  final CategoryModel category;

  const SubProductCategoryScreen(
      {super.key, required this.category, required this.productCategoryId});

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(ProductCategoryController());

    return Scaffold(
      appBar: EAppBar(title: Text(category.name, style: Theme.of(context).textTheme.headlineMedium,), showBackArrow: true,),
      body: FutureBuilder(
        future: controller.getBreedsByProductCategoryId(productCategoryId),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting)
            return const CircularProgressIndicator();
          if (snapshot.hasError)
            return Center(child: Text('Error: ${snapshot.error}'));

          final type = snapshot.data!;
          print('--------check ${type.length}');
          return ListView.builder(
            itemCount: type.length,
            itemBuilder: (_, i) => ListTile(title: Text(type[i].name)),
          );
        },
      ),
    );
  }
}
