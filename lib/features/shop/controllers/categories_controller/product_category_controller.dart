import 'dart:convert';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:pet_shop/data/config.dart';
import 'package:pet_shop/features/shop/models/categories/type_model.dart';
import 'package:pet_shop/features/shop/models/products/product_model.dart';

class ProductCategoryController extends GetxController {
  final baseUrl = Config.baseUrl;
  final _storage = GetStorage();
  final isLoad = false.obs;
  final featuredProducts = <ProductModel>[].obs;

  final Map<int, List<TypeModel>> _typesCache = {};

  Future<List<TypeModel>> getTypesByProductCategoryId(int productCategoryId) async {
    final url = Uri.parse('$baseUrl/product/category/$productCategoryId/type');
    final token = await _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');
    final response = await http.get(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => TypeModel.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load types');
    }
  }

  Future<List<ProductModel>> fetchProductByType(int categoryId, int typeId) async {
    final url = '$baseUrl/product/category/$categoryId/type/$typeId';
    final token = await _storage.read('TOKEN');

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        final decodedData = json.decode(response.body);

        if (decodedData is List) {
          List<TypeModel> types = _typesCache[categoryId] ??
              await getTypesByProductCategoryId(categoryId);

          final currentType = types.firstWhere(
                (type) => type.id == typeId,
            orElse: () => TypeModel(id: typeId, name: 'Unknown', productCategoryId: categoryId),
          );

          return decodedData
              .map((json) {
            final data = Map<String, dynamic>.from(json);
            data['type'] = {
              'id': currentType.id,
              'name': currentType.name,
            };
            return ProductModel.fromJson(data);
          })
              .cast<ProductModel>()
              .toList();
        } else {
          return [];
        }
      } else {
        throw Exception('Failed to load products');
      }
    } catch (e) {
      rethrow;
    }
  }
}
