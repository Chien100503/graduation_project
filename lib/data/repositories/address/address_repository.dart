import 'dart:convert';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:pet_shop/data/config.dart';

import '../../../features/personalizations/models/address_model.dart';

class AddressRepository extends GetxController {
  static AddressRepository get instance => Get.find();

  final String baseUrl = Config.baseUrl;
  final GetStorage storage = GetStorage();

  /// Get headers with authorization
  Map<String, String> _getHeaders() {
    final token = storage.read('TOKEN');
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    };
  }

  /// Get all user addresses
  Future<List<AddressModel>> getAllUserAddresses() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/address'),
        headers: _getHeaders(),
      );

      if (response.statusCode == 200) {
        final List<dynamic> addressList = json.decode(response.body);
        return addressList.map((json) => AddressModel.fromJson(json)).toList();
      } else if (response.statusCode == 401) {
        throw 'Unauthorized. Please login again.';
      } else {
        throw 'Failed to fetch addresses. Status: ${response.statusCode}';
      }
    } catch (e) {
      throw 'Something went wrong while fetching Address Information. Try again later.';
    }
  }

  /// Add new address
  Future<AddressModel> addAddress(AddressModel address) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/address'),
        headers: _getHeaders(),
        body: json.encode({
          'name': address.name,
          'phone': address.phone,
          'fullAddress': address.fullAddress,
          'isDefault': address.isDefault,
        }),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final responseData = json.decode(response.body);
        return AddressModel.fromJson(responseData);
      } else if (response.statusCode == 401) {
        throw 'Unauthorized. Please login again.';
      } else {
        throw 'Failed to add address. Status: ${response.statusCode}';
      }
    } catch (e) {
      throw 'Something went wrong while adding the address. Try again later.';
    }
  }

  /// Update address
  Future<AddressModel> updateAddress(AddressModel address) async {
    try {
      if (address.id == null) {
        throw 'Address ID is required for update';
      }

      final response = await http.put(
        Uri.parse('$baseUrl/address/${address.id}'),
        headers: _getHeaders(),
        body: json.encode({
          'name': address.name,
          'phone': address.phone,
          'fullAddress': address.fullAddress,
          'isDefault': address.isDefault,
        }),
      );

      if (response.statusCode == 200) {
        final responseData = json.decode(response.body);
        return AddressModel.fromJson(responseData);
      } else if (response.statusCode == 401) {
        throw 'Unauthorized. Please login again.';
      } else if (response.statusCode == 404) {
        throw 'Address not found.';
      } else {
        throw 'Failed to update address. Status: ${response.statusCode}';
      }
    } catch (e) {
      throw 'Something went wrong while updating the address. Try again later.';
    }
  }

  /// Delete address
  Future<void> deleteAddress(String addressId) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/address/$addressId'),
        headers: _getHeaders(),
      );

      if (response.statusCode == 200 || response.statusCode == 204) {
        // Success - address deleted
        return;
      } else if (response.statusCode == 401) {
        throw 'Unauthorized. Please login again.';
      } else if (response.statusCode == 404) {
        throw 'Address not found.';
      } else {
        throw 'Failed to delete address. Status: ${response.statusCode}';
      }
    } catch (e) {
      throw 'Something went wrong while deleting the address. Try again later.';
    }
  }

  /// Set address as default
  Future<ApiResponse> setDefaultAddress(String addressId) async {
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/address/$addressId'),
        headers: _getHeaders(),
        body: jsonEncode({'isDefault': true}),
      );

      if (response.statusCode == 200) {
        return ApiResponse(success: true, message: 'Success');
      } else if (response.statusCode == 401) {
        return ApiResponse(success: false, message: 'Unauthorized. Please login again.');
      } else if (response.statusCode == 404) {
        return ApiResponse(success: false, message: 'Address not found.');
      } else {
        return ApiResponse(success: false, message: 'Failed with status ${response.statusCode}');
      }
    } catch (e) {
      return ApiResponse(success: false, message: 'Something went wrong while setting default address.');
    }
  }
}
class ApiResponse {
  final bool success;
  final String message;
  ApiResponse({required this.success, required this.message});
}