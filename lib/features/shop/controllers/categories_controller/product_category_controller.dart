import 'dart:convert';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:get/get_state_manager/src/simple/get_controllers.dart';
import 'package:pet_shop/data/config.dart';
import 'package:pet_shop/features/shop/models/categories/type_model.dart';

class ProductCategoryController extends GetxController {
  final baseUrl = Config.baseUrl;
  final _storage = GetStorage();

  Future<List<TypeModel>> getBreedsByProductCategoryId(int productCategoryId) async {
    final url = Uri.parse('$baseUrl/product/categories/$productCategoryId/types');
    print('url $url');
    final token = await _storage.read('TOKEN'); // Sử dụng await
    if (token == null) throw Exception('Token không tồn tại.');
    final response = await http.get(url, headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    },);
    print('Respomse $response');
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => TypeModel.fromJson(json)).toList();

    } else {
      throw Exception('Failed to load breedssss');
    }
  }
  // Future<List<TypeModel>> fetchBrands() async {
  //   final response = await http.get(Uri.parse('$url/product/categories/$/types'));
  //   final data = jsonDecode(response.body) as List;
  //   return data.map((e) => TypeModel.fromJson(e)).toList();
  // }
}
