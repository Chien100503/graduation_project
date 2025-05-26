import '../../../utils/formatters/formatter.dart';

class UserModel {
  final int id;
  final String name;
  final String firstName;
  final String lastName;
  final String email;
  final String phone;
  final String avatar;

  UserModel({
    required this.id,
    required this.name,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.phone,
    required this.avatar,
  });

  /// Trả về tên đầy đủ
  String get fullName => '$firstName $lastName';

  /// Định dạng số điện thoại
  String get formattedPhoneNumber => EFormatter.formatPhoneNumber(phone);

  /// Tách tên
  static List<String> nameParts(String fullName) => fullName.split(' ');

  /// Tạo username
  static String generateUsername(String fullName) {
    List<String> parts = fullName.split(' ');
    String first = parts.isNotEmpty ? parts[0].toLowerCase() : '';
    String last = parts.length > 1 ? parts[1].toLowerCase() : '';
    return 'cwt_${first}${last}';
  }


  /// Parse từ JSON
  factory UserModel.fromJson(Map<String, dynamic> json) {
    print('📦 JSON from server: $json');

    return UserModel(
      id: _parseId(json['id']),
      name: _parseString(json['name']),
      firstName: _parseString(json['firstName']),
      lastName: _parseString(json['lastName']),
      email: _parseString(json['email']),
      phone: _parseString(json['phone']),
      avatar: _parseString(json['avatar']),
    );
  }

  /// Parse ID
  static int _parseId(dynamic value) {
    if (value is int) return value;
    if (value is String) return int.tryParse(value) ?? 0;
    return 0;
  }

  /// Parse String
  static String _parseString(dynamic value) {
    if (value == null || value == 'null') return '';
    if (value is String) return value;
    return value.toString();
  }

  /// Mặc định rỗng
  static UserModel empty() => UserModel(
    id: 0,
    name: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    avatar: '',
  );

  /// Convert thành JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'firstName': firstName,
      'lastName': lastName,
      'fullName': fullName,
      'email': email,
      'phone': phone,
      'avatar': avatar,
    };
  }

  /// Bản sao mới với giá trị cập nhật
  UserModel copyWith({
    int? id,
    String? name,
    String? firstName,
    String? lastName,
    String? email,
    String? phone,
    String? avatar,
  }) {
    return UserModel(
      id: id ?? this.id,
      name: name ?? this.name,
      firstName: firstName ?? this.firstName,
      lastName: lastName ?? this.lastName,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      avatar: avatar ?? this.avatar,
    );
  }
}
