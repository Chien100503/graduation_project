import 'dart:convert';

import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:pet_shop/data/config.dart';
import '../../models/categories/breed_model.dart';
import 'package:http/http.dart' as http;

import '../../models/products/pet_model.dart';

class PetCategoryController extends GetxController {
  var isLoading = true.obs;
  final baseUrl = Config.baseUrl;
  final _storage = GetStorage();

  Future<List<BreedModel>> fetchBreedsByCategory(int petCategoryId) async {
    final url = Uri.parse('$baseUrl/pet/categories/$petCategoryId/breeds');
    final token = await _storage.read('TOKEN'); // Sử dụng await
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
      return jsonList.map((json) => BreedModel.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load breedssss');
    }
  }

  Future<List<PetModel>> fetchPetsByBreed(int categoryId, int breedId) async {
    final url = ('$baseUrl/pet/categories/$categoryId/breeds/$breedId');
    final token = await _storage.read('TOKEN');
    print(url);
    final response = await http.get(Uri.parse(url,),  headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    },);
    
    print('g: $response');
    if (response.statusCode == 200) {
      final List data = json.decode(response.body);
      
      return data.map((json) => PetModel.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load pets for breed $breedId');
    }
  }
}
