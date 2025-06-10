import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:get_storage/get_storage.dart';
import 'package:pet_shop/features/shop/models/categories/brand_model.dart';
import 'package:pet_shop/features/shop/models/categories/breed_model.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import 'package:pet_shop/features/shop/models/products/product_detail_model.dart';
import '../../../data/config.dart';
import '../../../features/shop/models/products/pet_model.dart';

class CategoryRepository {
  final String _baseUrl = Config.baseUrl;
  final GetStorage _storage = GetStorage();

  Future<List<dynamic>> fetchAllCategories() async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final response = await http.get(
      Uri.parse('$_baseUrl/category'),
      headers: {
        'Accept': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data;
    } else {
      throw Exception('Failed to load categories: HTTP ${response.statusCode}');
    }
  }

  Future<List<PetDetailModel>> getAllPetsByCategory(String categoryId) async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final response = await http.get(
      Uri.parse('$_baseUrl/pet/category/$categoryId'),
      headers: {
        'Accept': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as List;
      return data.map((json) => PetDetailModel.fromJson(json)).toList();
    } else {
      throw Exception(
          'Failed to load pets for category $categoryId: HTTP ${response.statusCode}');
    }
  }

  Future<List<ProductDetailModel>> getAllProductsByCategory(
      String categoryId) async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final url = http.get(
      Uri.parse('$_baseUrl/product/category/$categoryId'),
      headers: {
        'Accept': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    print('url: $url');
    final response = await url;

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as List;
      return data.map((json) => ProductDetailModel.fromJson(json)).toList();
    } else {
      throw Exception(
          'Failed to load pets for category $categoryId: HTTP ${response.statusCode}');
    }
  }

  Future<List<BreedModel>> getAllBreedById(String breedId) async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final url = http.get(
      Uri.parse('$_baseUrl/pet/category/$breedId/breeds'),
      headers: {
        'Accept': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    final response = await url;
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as List;
      return data.map((json) => BreedModel.fromJson(json)).toList();
    } else {
      throw Exception(
          'Failed to load pets for category $breedId: HTTP ${response.statusCode}');
    }
  }
  
  // Future<List<>> getAllBrand ({required String categoryId, required String breedId}) async {
  //   final token = _storage.read('TOKEN')
  //   final url = http.get(
  //     Uri.parse('$_baseUrl/pet/category/$categoryId/breeds/$breedId'),
  //     headers: {
  //       'Accept': 'application/json',
  //       'Authorization': 'Bearer $token',
  //     },
  //   );
  //   final response = await url;
  //   if (response.statusCode == 200) {
  //     final data = jsonDecode(response.body) as List;
  //     return data.map((json) => PetModel.fromJson(json)).toList();
  //   } else {
  //     throw Exception(
  //         'Failed to load pets for category $breedId: HTTP ${response.statusCode}');
  //   }
  // }
  Future<List<BrandModel>> getAllBrand() async {
    final token = await _storage.read('TOKEN');

    if (token == null) {
      throw Exception('Token không tồn tại.');
    }

    final url = Uri.parse('$_baseUrl/api/product/brands');

    try {
      final response = await http.get(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = jsonDecode(response.body);

        return jsonList
            .map((json) => BrandModel.fromJson(json))
            .toList();
      } else {
        print('Lỗi HTTP: ${response.statusCode}');
        print('Body: ${response.body}');
        throw Exception('Không thể tải danh sách thương hiệu.');
      }
    } catch (e) {
      print('Lỗi trong getAllBrand: $e');
      rethrow;
    }
  }
}
