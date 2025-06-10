// lib/features/shop/controllers/product_category_controller.dart

import 'package:get/get.dart';
import 'package:pet_shop/data/repositories/categories/category_product_repository.dart';
import 'package:pet_shop/features/shop/models/categories/type_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_model.dart';

class ProductCategoryController extends GetxController {
  final CategoryProductRepository _repository = CategoryProductRepository();

  final isLoad = false.obs;
  // final featuredProducts = <ProductDetailModel>[].obs;
  final Map<int, List<TypeModel>> _typesCache = {};

  Future<List<TypeModel>> getTypes(int productCategoryId) async {
    if (_typesCache.containsKey(productCategoryId)) {
      return _typesCache[productCategoryId]!;
    }

    final types = await _repository.getTypesByProductCategoryId(productCategoryId);
    _typesCache[productCategoryId] = types;
    return types;
  }

  Future<List<ProductDetailModel>> getProductsByType(int categoryId, int typeId) async {
    final products = await _repository.fetchProductByType(categoryId, typeId);

    List<TypeModel> types = _typesCache[categoryId] ??
        await getTypes(categoryId);

    final currentType = types.firstWhere(
          (type) => type.id == typeId,
      orElse: () => TypeModel(id: typeId, name: 'Unknown', productCategoryId: categoryId),
    );

    return products.map((product) {
      final json = product.toJson();
      json['type'] = {
        'id': currentType.id,
        'name': currentType.name,
      };
      return ProductDetailModel.fromJson(json);
    }).toList();
  }

  void clearCache() => _typesCache.clear();
}
