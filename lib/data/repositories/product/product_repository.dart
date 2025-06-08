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
    print('Requesting: $url');
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
        print('Fetched ${jsonList.length} products.');

        return jsonList.map((json) {
          try {
            return ProductDetailModel.fromJson(json);
          } catch (e) {
            print('Error parsing item: $e');
            print('Raw item: $json');
            return null;
          }
        }).whereType<ProductDetailModel>().toList();
      } else {
        print('Server error: ${response.statusCode}');
        print('Response body: ${response.body}');
        throw Exception('Failed to load products');
      }
    } catch (e) {
      print('Exception in getAllProducts: $e');
      rethrow;
    }
  }
}
