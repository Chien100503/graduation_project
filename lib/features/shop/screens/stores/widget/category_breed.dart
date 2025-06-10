// import 'package:flutter/material.dart';
// import 'package:pet_shop/common/widgets/shimmer/shimmer_list_title.dart';
// import 'package:pet_shop/features/shop/controllers/categories_controller/category_controller.dart';
// import 'package:pet_shop/features/shop/models/categories/breed_model.dart';
//
// import '../../../../../common/widgets/shimmer/shimmer_boxes.dart';
// import '../../../../../utils/constants/sizes.dart';
// import '../../../../../utils/helpers/cloud_helper_functions.dart';
//
// class CategoryBreed extends StatelessWidget {
//   const CategoryBreed({super.key, required this.breed});
//
//   final BreedModel breed;
//
//   @override
//   Widget build(BuildContext context) {
//     final controller = CategoryController.instance;
//     return FutureBuilder(
//       future: controller.getAllBreedById(breedId: breed.id.toString()),
//       builder: (context, snapshot) {
//         const loader = Column(
//           children: [
//             ShimmerListTitle(),
//             SizedBox(height: ESizes.defaultBetweenItem),
//             ShimmerBoxes(),
//             SizedBox(height: ESizes.defaultBetweenItem),
//           ],
//         );
//         final widget = CloudHelperFunctions.checkMultiRecordState(
//             snapshot: snapshot, loader: loader);
//         if (widget != null) return widget;
//
//         //Record found!
//         final breeds = snapshot.data!;
//         return ListView.builder(
//           shrinkWrap: true,
//           physics: const NeverScrollableScrollPhysics(),
//           itemCount: breeds.length,
//           itemBuilder: (_, index) {
//             final breed = breeds[index];
//             return FutureBuilder(
//               future: controller.getBrandProduct(brandId: brand.id, limit: 3),
//               builder: (context, snapshot) {
//                 final widget = CloudHelperFunctions.checkMultiRecordState(
//                     snapshot: snapshot, loader: loader);
//                 if (widget != null) return widget;
//
//                 // Record found!
//                 final products = snapshot.data!;
//
//                 return EBrandShowCase(
//                   brand: brand,
//                   images: products.map((e) => e.thumbnail).toList(),
//                 );
//               },
//             );
//           },
//         );
//       },
//     );
//   }
// }
