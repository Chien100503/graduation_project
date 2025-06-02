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

  // Cache để lưu breeds theo category
  final Map<int, List<BreedModel>> _breedsCache = {};

  Future<List<BreedModel>> fetchBreedsByCategory(int petCategoryId) async {
    if (_breedsCache.containsKey(petCategoryId)) {
      return _breedsCache[petCategoryId]!;
    }

    final url = Uri.parse('$baseUrl/pet/category/$petCategoryId/breeds');
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
      final breeds = jsonList.map((json) => BreedModel.fromJson(json)).toList();

      _breedsCache[petCategoryId] = breeds;
      return breeds;
    } else {
      throw Exception('Failed to load breeds');
    }
  }

  Future<List<PetModel>> fetchPetsByBreed(int categoryId, int breedId) async {
    final url = ('$baseUrl/pet/category/$categoryId/breeds/$breedId');
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
        if (response.body.isEmpty) {
          print('Response body is empty');
          return [];
        }

        final dynamic decodedData = json.decode(response.body);
        print('Decoded data type: ${decodedData.runtimeType}');

        if (decodedData is List) {
          if (decodedData.isEmpty) {
            print('Data list is empty');
            return [];
          }

          // Lấy breed info từ cache hoặc fetch mới
          List<BreedModel> breeds;
          if (_breedsCache.containsKey(categoryId)) {
            breeds = _breedsCache[categoryId]!;
          } else {
            breeds = await fetchBreedsByCategory(categoryId);
          }

          final currentBreed = breeds.firstWhere(
                (breed) => breed.id == breedId,
            orElse: () => BreedModel(id: breedId, name: 'Unknown Breed',petCategoryId: categoryId),
          );

          return decodedData
              .where((item) => item != null)
              .map((json) {
            try {
              final Map<String, dynamic> petData = Map<String, dynamic>.from(json);
              petData['breed'] = {
                'id': currentBreed.id,
                'name': currentBreed.name,
              };

              return PetModel.fromJson(petData);
            } catch (e) {
              print('Error parsing pet item: $e');
              print('Item data: $json');
              return null;
            }
          })
              .where((pet) => pet != null)
              .cast<PetModel>()
              .toList();
        } else {
          print('Response data is not a List: ${decodedData.runtimeType}');
          return [];
        }
      } else {
        print('HTTP Error: ${response.statusCode}');
        print('Error body: ${response.body}');
        throw Exception('Failed to load pets for breed $breedId. Status: ${response.statusCode}');
      }
    } catch (e) {
      print('Exception in fetchPetsByBreed: $e');
      rethrow;
    }
  }

  // Method để clear cache khi cần
  void clearCache() {
    _breedsCache.clear();
  }
}