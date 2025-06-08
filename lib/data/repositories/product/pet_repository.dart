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
        print('Fetched ${jsonList.length} pets.');

        return jsonList.map((json) {
          try {
            return PetDetailModel.fromJson(json);
          } catch (e) {
            print('Error parsing pet item: $e');
            print('Raw item: $json');
            return null;
          }
        }).whereType<PetDetailModel>().toList();
      } else {
        print('Server error: ${response.statusCode}');
        print('Response body: ${response.body}');
        throw Exception('Failed to load pets');
      }
    } catch (e) {
      print('Exception in getAllPets: $e');
      rethrow;
    }
  }
}
