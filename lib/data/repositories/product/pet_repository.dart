import 'dart:convert';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';
import '../../config.dart';

class PetRepository extends GetxController {
  final String _baseUrl = Config.baseUrl;
  final GetStorage _storage = GetStorage();

  Future<List<PetDetailModel>> getAllPets() async {
    final url = Uri.parse('$_baseUrl/pet');
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
            return PetDetailModel.fromJson(json);
          } catch (e) {
            return null;
          }
        }).whereType<PetDetailModel>().toList();
      } else {
        throw Exception('Failed to load pets');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<PetDetailModel> getPetById(String petId) async {
    final url = Uri.parse('$_baseUrl/pet/$petId');

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

        final petModel = PetDetailModel.fromJson(jsonData);

        return petModel;
      } else {
        throw Exception('Failed to load product detail - Status: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }
}
