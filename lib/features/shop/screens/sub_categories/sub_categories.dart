// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import 'package:pet_shop/features/shop/models/category_models.dart';
// import 'package:pet_shop/features/shop/controllers/category_controller.dart';
//
// class SubCategoryScreen extends StatelessWidget {
//   const SubCategoryScreen({super.key, required this.category});
//
//   final PetCategoryModel category;
//
//   @override
//   Widget build(BuildContext context) {
//     final controller = Get.put(PetCategoryController());
//
//     return Scaffold(
//       appBar: AppBar(
//         title: Text(category.name),
//         leading: IconButton(
//           icon: const Icon(Icons.arrow_back),
//           onPressed: () => Get.back(),
//         ),
//       ),
//       body: SingleChildScrollView(
//         child: Padding(
//           padding: const EdgeInsets.all(16.0),
//           child: Column(
//             children: [
//               // Banner Image
//               Container(
//                 width: double.infinity,
//                 height: 200,
//                 decoration: BoxDecoration(
//                   borderRadius: BorderRadius.circular(12),
//                   color: Colors.grey[200],
//                 ),
//                 child: category.image != null && category.image!.isNotEmpty
//                     ? ClipRRect(
//                   borderRadius: BorderRadius.circular(12),
//                   child: Image.network(
//                     category.image!,
//                     fit: BoxFit.cover,
//                     errorBuilder: (context, error, stackTrace) {
//                       return Center(
//                         child: Column(
//                           mainAxisAlignment: MainAxisAlignment.center,
//                           children: [
//                             Icon(Icons.pets, size: 48, color: Colors.grey[600]),
//                             const SizedBox(height: 8),
//                             Text(
//                               category.name,
//                               style: Theme.of(context).textTheme.headlineSmall,
//                             ),
//                           ],
//                         ),
//                       );
//                     },
//                   ),
//                 )
//                     : Center(
//                   child: Column(
//                     mainAxisAlignment: MainAxisAlignment.center,
//                     children: [
//                       Icon(Icons.pets, size: 48, color: Colors.grey[600]),
//                       const SizedBox(height: 8),
//                       Text(
//                         category.name,
//                         style: Theme.of(context).textTheme.headlineSmall,
//                       ),
//                     ],
//                   ),
//                 ),
//               ),
//
//               const SizedBox(height: 24),
//
//               // Category Info Section
//               Card(
//                 child: Padding(
//                   padding: const EdgeInsets.all(16.0),
//                   child: Column(
//                     crossAxisAlignment: CrossAxisAlignment.start,
//                     children: [
//                       Text(
//                         'Category: ${category.name}',
//                         style: Theme.of(context).textTheme.headlineSmall,
//                       ),
//                       const SizedBox(height: 8),
//                       Text(
//                         'Category ID: ${category.id}',
//                         style: Theme.of(context).textTheme.bodyMedium?.copyWith(
//                           color: Colors.grey[600],
//                         ),
//                       ),
//                       const SizedBox(height: 16),
//
//                       // Placeholder for products - you'll need to implement this
//                       Text(
//                         'Products in this category:',
//                         style: Theme.of(context).textTheme.titleMedium,
//                       ),
//                       const SizedBox(height: 12),
//
//                       // TODO: Implement product loading based on category ID
//                       Container(
//                         width: double.infinity,
//                         padding: const EdgeInsets.all(20),
//                         decoration: BoxDecoration(
//                           color: Colors.grey[100],
//                           borderRadius: BorderRadius.circular(8),
//                         ),
//                         child: Column(
//                           children: [
//                             Icon(Icons.inventory_2_outlined,
//                                 size: 48, color: Colors.grey[500]),
//                             const SizedBox(height: 8),
//                             Text(
//                               'Products will be loaded here',
//                               style: Theme.of(context).textTheme.bodyMedium?.copyWith(
//                                 color: Colors.grey[600],
//                               ),
//                             ),
//                             const SizedBox(height: 8),
//                             Text(
//                               'Category ID: ${category.id}',
//                               style: Theme.of(context).textTheme.bodySmall?.copyWith(
//                                 color: Colors.grey[500],
//                               ),
//                             ),
//                           ],
//                         ),
//                       ),
//                     ],
//                   ),
//                 ),
//               ),
//
//               const SizedBox(height: 16),
//
//               // Action Buttons
//               Row(
//                 children: [
//                   Expanded(
//                     child: ElevatedButton.icon(
//                       onPressed: () {
//                         // TODO: Navigate to all products in this category
//                         Get.snackbar(
//                           'Info',
//                           'Navigate to all products in ${category.name}',
//                           snackPosition: SnackPosition.BOTTOM,
//                         );
//                       },
//                       icon: const Icon(Icons.list),
//                       label: const Text('View All Products'),
//                     ),
//                   ),
//                   const SizedBox(width: 12),
//                   Expanded(
//                     child: OutlinedButton.icon(
//                       onPressed: () {
//                         // TODO: Add to favorites or other action
//                         Get.snackbar(
//                           'Info',
//                           'Feature coming soon!',
//                           snackPosition: SnackPosition.BOTTOM,
//                         );
//                       },
//                       icon: const Icon(Icons.favorite_border),
//                       label: const Text('Add to Favorites'),
//                     ),
//                   ),
//                 ],
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
// }