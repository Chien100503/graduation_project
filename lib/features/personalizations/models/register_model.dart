class RegisterModel {
  final String name;
  final String firstName;
  final String lastName;
  final String email;
  final String phone;
  final String password;
  final String confirmPassword;

  RegisterModel({
    required this.firstName,
    required this.name,
    required this.lastName,
    required this.email,
    required this.phone,
    required this.password,
    required this.confirmPassword,
  });

  Map<String, dynamic> toJson() {
    return {
      'firstName': firstName,
      'lastName': lastName,
      'name': name,
      'email': email,
      'phone': phone,
      'password': password,
      'confirmPassword': confirmPassword,
    };
  }
}
