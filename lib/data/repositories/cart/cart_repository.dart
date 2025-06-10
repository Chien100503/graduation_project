import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import '../../../features/checkout/models/cart_item_model.dart';
import '../../config.dart';

class CartRepository {
  final String _baseUrl = Config.baseUrl;
  final GetStorage _storage = GetStorage();
  final Dio _dio = Dio();

  Future<void> addToCart({
    int? productId,
    int? petId,
    required double price,
    required int quantity,
  }) async {
    try {
      final token = _storage.read('TOKEN');
      if (kDebugMode) {
        print("check token $token");
      }
      if (token == null) throw Exception('Token không tồn tại');

      final headers = {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      };

      final body = {
        if (productId != null) 'productId': productId,
        if (petId != null) 'petId': petId,
        'price': price,
        'quantity': quantity,
      };

      final response = await _dio.post(
        '$_baseUrl/cart/add',
        data: body,
        options: Options(headers: headers),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        if (kDebugMode) {
          print('Thêm giỏ hàng thành công');
        }
        Get.snackbar(
          'Good!', 'Thêm giỏ hàng thành công',
          backgroundColor: Colors.green,
          colorText: Colors.white,
          snackPosition: SnackPosition.TOP,
          icon: const Icon(Icons.check_circle, color: Colors.white),
          borderRadius: 8,
          margin: const EdgeInsets.all(16),
          duration: const Duration(seconds: 2),
        );
      } else {
        throw Exception('Thêm giỏ hàng thất bại (${response.statusCode})');
      }
    } catch (e) {
      if (kDebugMode) {
        print('Lỗi, Không thể thêm vào giỏ hàng $e' );
      }
      Get.snackbar(
        'Lỗi', 'Không thể thêm vào giỏ hàng',
        backgroundColor: Colors.red,
        colorText: Colors.white,
        snackPosition: SnackPosition.TOP,
        icon: const Icon(Icons.check_circle, color: Colors.white),
        borderRadius: 8,
        margin: const EdgeInsets.all(16),
        duration: const Duration(seconds: 2),
      );
    }
  }
  Future<List<CartItemModel>> getCartItems() async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại');

    final response = await _dio.get(
      '$_baseUrl/cart',
      options: Options(headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      }),
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = response.data;
      final List<dynamic> items = data['items']; // ✅ Lấy danh sách từ key 'items'

      return items.map((json) => CartItemModel.fromJson(json)).toList();
    } else {
      throw Exception('Không thể lấy giỏ hàng');
    }
  }


}
