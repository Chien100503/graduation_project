class AddressModel {
  final int? id;
  final String name;
  final String recipientName;
  final String phone;
  final String fullAddress;
  final bool isDefault;

  AddressModel({
    this.id,
    required this.name,
    required this.recipientName,
    required this.phone,
    required this.fullAddress,
    this.isDefault = false,
  });

  // Convert from JSON
  factory AddressModel.fromJson(Map<String, dynamic> json) {
    return AddressModel(
      id: json['id'],
      name: json['name'] ?? '',
      recipientName: json['recipientName'] ?? '',
      phone: json['phone'] ?? '',
      fullAddress: json['fullAddress'] ?? '',
      isDefault: json['default'] ?? false,
    );
  }

  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'recipientName': recipientName,
      'phone': phone,
      'fullAddress': fullAddress,
      'default': isDefault,
    };
  }

  // Create a copy with updated fields
  AddressModel copyWith({
    int? id,
    String? name,
    String? recipientName,
    String? phone,
    String? fullAddress,
    bool? isDefault,
  }) {
    return AddressModel(
      id: id ?? this.id,
      name: name ?? this.name,
      recipientName: recipientName ?? this.recipientName,
      phone: phone ?? this.phone,
      fullAddress: fullAddress ?? this.fullAddress,
      isDefault: isDefault ?? this.isDefault,
    );
  }

  @override
  String toString() {
    return 'AddressModel(id: $id, name: $name, phone: $phone, fullAddress: $fullAddress, default: $isDefault)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is AddressModel &&
        other.id == id &&
        other.name == name &&
        other.phone == phone &&
        other.fullAddress == fullAddress &&
        other.isDefault == isDefault;
  }
  factory AddressModel.empty() {
    return AddressModel(
      name: '',
      phone: '',
      fullAddress: '',
      isDefault: false, recipientName: '',
    );
  }

  @override
  int get hashCode {
    return id.hashCode ^
    name.hashCode ^
    recipientName.hashCode ^
    phone.hashCode ^
    fullAddress.hashCode ^
    isDefault.hashCode;
  }
}