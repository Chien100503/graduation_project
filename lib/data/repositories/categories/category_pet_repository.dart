import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:get_storage/get_storage.dart';
import 'package:pet_shop/data/config.dart';
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';

import '../../../features/shop/models/categories/breed_model.dart';


class CategoryPetRepository {
  final baseUrl = Config.baseUrl;
  final _storage = GetStorage();
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

  Future<List<PetDetailModel>> fetchPetsByBreed(int categoryId, int breedId) async {
    final url = ('$baseUrl/pet/category/$categoryId/breeds/$breedId');
    final token = await _storage.read('TOKEN');

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
        List<BreedModel> breeds =
            _breedsCache[categoryId] ?? await fetchBreedsByCategory(categoryId);

        final currentBreed = breeds.firstWhere(
              (breed) => breed.id == breedId,
          orElse: () => BreedModel(id: breedId, name: 'Unknown Breed', petCategoryId: categoryId),
        );

        return decodedData
            .where((item) => item != null)
            .map((json) {
          final Map<String, dynamic> petData = Map<String, dynamic>.from(json);
          petData['breed'] = {
            'id': currentBreed.id,
            'name': currentBreed.name,
          };
          return PetDetailModel.fromJson(petData);
        })
            .cast<PetDetailModel>()
            .toList();
      } else {
        return [];
      }
    } else {
      throw Exception('Failed to load pets for breed $breedId. Status: ${response.statusCode}');
    }
  }

  void clearCache() => _breedsCache.clear();
}
