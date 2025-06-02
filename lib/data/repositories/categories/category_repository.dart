import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:get_storage/get_storage.dart';
import '../../../data/config.dart';

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
      throw Exception(
          'Failed to load categories: HTTP ${response.statusCode}');
    }
  }
}
