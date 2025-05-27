import 'dart:convert';

import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:pet_shop/data/config.dart';
import 'package:pet_shop/features/shop/models/banners/banner_model.dart';

class BannerRepository {
  final _storage = GetStorage();
  final baseUrl = Config.baseUrl;

  Future<List<BannerModel>> fetchAllBanner() async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final response = await http.get(
      Uri.parse('$baseUrl/banner'),
      headers: {
        'Accept': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => BannerModel.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load categories: HTTP ${response.statusCode}');
    }
  }
}
