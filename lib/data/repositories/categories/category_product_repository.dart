// lib/features/shop/repository/product_repository.dart

import 'dart:convert';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:pet_shop/data/config.dart';
import 'package:pet_shop/features/shop/models/categories/type_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_model.dart';

class CategoryProductRepository {
  final baseUrl = Config.baseUrl;
  final _storage = GetStorage();

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

  Future<List<ProductDetailModel>> fetchProductByType(int categoryId, int typeId) async {
    final url = '$baseUrl/product/category/$categoryId/type/$typeId';
    final token = await _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

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
        return decodedData
            .map((json) => ProductDetailModel.fromJson(Map<String, dynamic>.from(json)))
            .toList();
      }
      return [];
    } else {
      throw Exception('Failed to load products');
    }
  }
}
