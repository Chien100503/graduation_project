import 'dart:convert';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;

import '../../config.dart';

class PaymentRepository {
  final String _baseUrl = Config.baseUrl;
  final GetStorage _storage = GetStorage();

  Future<http.Response> createOrder({
    required int addressId,
    required String paymentMethod,
  }) async {
    final url = Uri.parse('$_baseUrl/order/create');
    final token = _storage.read('TOKEN');

    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode({
        'addressId': addressId,
        'paymentMethod': paymentMethod,
      }),
    );
        print('Sending order: addressId=$addressId, paymentMethod=$paymentMethod');

    return response;
  }
}
