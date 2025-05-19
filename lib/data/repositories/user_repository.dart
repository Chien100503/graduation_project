import 'dart:convert';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import '../../features/personalizations/models/register_model.dart';
import '../../features/personalizations/models/user_model.dart';

class UserRepository {
  final String _baseUrl = 'http://192.168.2.121:8080/api';
  final GetStorage _storage = GetStorage();

  //  ------  LOGIN USER + SAVE TOKEN ------
  Future<Map<String, dynamic>> loginUser(String email, String password) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );

    print('STATUS CODE: ${response.statusCode}');
    print('RESPONSE BODY: ${response.body}');

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
      try {
        final errorData = jsonDecode(response.body);
        final message = errorData['message'] ?? 'Login failed';
        throw Exception('Login failed: $message');
      } catch (e) {
        throw Exception('Login failed with status ${response.statusCode}');
      }
    }
  }

  // ------  REGISTER USER ------
  Future<void> registerUser(RegisterModel model) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(model.toJson()),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      final responseData = jsonDecode(response.body);
      final token = responseData['token'];
      final isActive = responseData['isActive'];
      final id = responseData[
          'id']; // Hoặc responseData['user']['id'] tùy theo backend trả về

      if (token != null) {
        _storage.write('TOKEN', token);

        // In ra token, isActive, id
        print('--- Đăng ký thành công ---');
        print('Token: $token');
        print('isActive: $isActive');
        print('ID: $id');
      }
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Đăng ký thất bại');
    }
  }

  // ------  VERIFY PIN CODE ------
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

    print('🔐 Token được gửi: $token');

    if (response.statusCode == 200 || response.statusCode == 201) {
      try {
        final responseData = jsonDecode(response.body);
        print('📥 Raw response body: ${response.body}');
        print('🧾 Parsed JSON: $responseData');

        final message = responseData['message'];
        print('✅ Message = $message');

        return message == 'Xác thực thành công';
      } catch (e) {
        print('❌ JSON không hợp lệ: ${response.body}');
        return false;
      }
    } else {
      print('❌ Xác minh thất bại: ${response.statusCode}');
      print('❌ Phản hồi: ${response.body}');
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

    print('📨 Resend code status: ${response.statusCode}');
    print('📨 Resend code response: ${response.body}');

    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Không thể gửi lại mã PIN');
    }
  }

  // Đăng xuất người dùng
  Future<void> logout() async {
    final token = _storage.read('TOKEN');
    if (token == null) {
      print('⚠️ Không có token để logout.');
      return;
    }
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/logout'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
      );
      print('🔐 Logout status: ${response.statusCode}');
      print('🔐 Logout response: ${response.body}');
      if (response.statusCode == 200) {
        await _storage.remove('TOKEN');
        final token = _storage.remove('TOKEN');
        await _storage.remove('USER_ID');
        print('Check token: $token');
        print('🚪 Đăng xuất thành công.');
      } else {
        final error = jsonDecode(response.body);
        throw Exception(error['message'] ?? 'Logout thất bại');
      }
    } catch (e) {
      print('❌ Lỗi khi logout: $e');
      rethrow;
    }
  }

  // Get all Profile of User
  Future<UserProfileModel> getProfile() async {
    final token = _storage.read('TOKEN');
    if (token == null) throw Exception('Token không tồn tại.');

    final response = await http.get(
      Uri.parse('$_baseUrl/profile'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as Map<String, dynamic>;
      print('📥 Full response: $data');
      return UserProfileModel.fromJson(data);
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Lấy thông tin thất bại');
    }
  }

  // ------ FORGOT PASSWORD ------
  Future<void> forgotPassword(String email) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/forgot-password'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email}),
    );

    print('📧 Forgot password status: ${response.statusCode}');
    print('📧 Forgot password response: ${response.body}');

    if (response.statusCode == 200) {
      final responseData = jsonDecode(response.body);
      final message = responseData['message'] ?? 'Mã yêu cầu đã được gửi đến email của bạn.';
      print('✅ Message: $message');
    } else {
      final errorData = jsonDecode(response.body);
      throw Exception(errorData['message'] ?? 'Gửi yêu cầu quên mật khẩu thất bại');
    }
  }
}
