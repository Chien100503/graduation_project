import 'dart:convert';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import '../../config.dart';

class ProductRepository extends GetxController {
  final String _baseUrl = Config.baseUrl;
  final GetStorage _storage = GetStorage();

  Future<List<ProductDetailModel>> getAllProducts() async {
    final url = Uri.parse('$_baseUrl/product');
    final token = _storage.read('TOKEN');

    try {
      final response = await http.get(
        url,
        headers: {
          'Content-Type': 'application/json',
          if (token != null) 'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = jsonDecode(response.body);

        return jsonList.map((json) {
          try {
            return ProductDetailModel.fromJson(json);
          } catch (e) {
            return null;
          }
        }).whereType<ProductDetailModel>().toList();
      } else {
        throw Exception('Failed to load products');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Thêm method mới để lấy chi tiết sản phẩm theo ID
  Future<ProductDetailModel> getProductById(String productId) async {
    final url = Uri.parse('$_baseUrl/product/$productId');

    final token = _storage.read('TOKEN');

    try {
      final response = await http.get(
        url,
        headers: {
          'Content-Type': 'application/json',
          if (token != null) 'Authorization': 'Bearer $token',
        },
      );


      if (response.statusCode == 200) {

        final Map<String, dynamic> jsonData = jsonDecode(response.body);

        // Debug từng field quan trọng

        // In ra tất cả keys trong JSON
        jsonData.keys.forEach((key) {
        });

        final productModel = ProductDetailModel.fromJson(jsonData);

        return productModel;
      } else {
        throw Exception('Failed to load product detail - Status: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }
}