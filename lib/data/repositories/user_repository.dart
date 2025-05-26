import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:http_parser/http_parser.dart';
import 'package:dio/dio.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:mime/mime.dart';
import '../../features/personalizations/models/register_model.dart';
import '../../features/personalizations/models/user_model.dart';

class UserRepository {
  final String _baseUrl = 'http://192.168.2.121:8080/api';
  final GetStorage _storage = GetStorage();

  // ------ LOGIN USER ------
  Future<Map<String, dynamic>> loginUser(String email, String password) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final isActive = data['isActive'];
      if (isActive == true) {
        _storage.write('TOKEN', data['token']);
        _storage.write('USER_ID', data['id']);
        return data;
      } else {
        throw Exception(
            'Tài khoản chưa được kích hoạt, vui lòng xác thực email.');
      }
    } else {
      final errorData = jsonDecode(response.body);
      throw Exception(
          'Login failed: ${errorData['message'] ?? 'Không xác định'}');
    }
  }

  // ------ REGISTER USER ------
  Future<void> registerUser(RegisterModel model) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(model.toJson()),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      final responseData = jsonDecode(response.body);
      final token = responseData['token'];
      if (token != null) {
        _storage.write('TOKEN', token);
        _storage.write('USER_ID', responseData['id']);
        print('Đăng ký thành công');
      }
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Đăng ký thất bại');
    }
  }

  // ------ VERIFY PIN CODE ------
  Future<bool> verifyPinCode(String code) async {
    final token = _storage.read('TOKEN');
    final response = await http.post(
      Uri.parse('$_baseUrl/verify'),
      headers: {
        'Content-Type': 'application/json',
        if (token != null) 'Authorization': 'Bearer $token',
      },
      body: jsonEncode({'code': code}),
    );

    if (response.statusCode == 200) {
      final responseData = jsonDecode(response.body);
      return responseData['message'] == 'Xác thực thành công';
    } else {
      return false;
    }
  }

  // ------ RESEND PINCODE ------
  Future<void> resendCode() async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Không tìm thấy token.');

    final response = await http.post(
      Uri.parse('$_baseUrl/resend'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Không thể gửi lại mã PIN');
    }
  }

  // ------ LOGOUT ------
  Future<void> logout() async {
    final token = _storage.read('TOKEN');
    if (token == null) return;

    final response = await http.post(
      Uri.parse('$_baseUrl/logout'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      await _storage.remove('TOKEN');
      await _storage.remove('USER_ID');
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Logout thất bại');
    }
  }

  // ------ FORGOT PASSWORD ------
  Future<void> forgotPassword(String email) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/forgot-password'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email}),
    );

    if (response.statusCode != 200) {
      final errorData = jsonDecode(response.body);
      throw Exception(
          errorData['message'] ?? 'Gửi yêu cầu quên mật khẩu thất bại');
    }
  }

  // ------ GET PROFILE ------
  Future<UserModel> getProfile() async {
    final token = await _storage.read('TOKEN'); // Sử dụng await
    if (token == null) throw Exception('Token không tồn tại.');

    final response = await http.get(
      Uri.parse('$_baseUrl/profile'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return UserModel.fromJson(data);
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Lấy thông tin thất bại');
    }
  }

  // ✅ ------ UPDATE PROFILE DÙNG FORM-DATA (DIO) ------
  Future<void> updateProfileDio({
    String? firstName,
    String? lastName,
    String? name,
    String? phone,
    File? avatarFile,
  }) async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final dio = Dio();

    if (avatarFile != null) {
      final mimeType = lookupMimeType(avatarFile.path);
      final mediaType = mimeType != null
          ? MediaType.parse(mimeType)
          : MediaType('application', 'octet-stream');

      final formData = FormData.fromMap({
        'firstName': firstName,
        'lastName': lastName,
        'phone': phone,
        'name': name,
        'avatar': await MultipartFile.fromFile(
          avatarFile.path,
          filename: avatarFile.path
              .split('/')
              .last,
          contentType: mediaType,
        ),
      });

      try {
        final response = await dio.put(
          '$_baseUrl/profile',
          data: formData,
          options: Options(
            headers: {
              'Authorization': 'Bearer $token',
              'Content-Type': 'multipart/form-data'
            },
          ),
        );
        print('Hồ sơ đã được cập nhật: ${response.data}');
      } catch (e) {
        print('Cập nhật hồ sơ thất bại: $e');
        rethrow;
      }
    }
  }
}