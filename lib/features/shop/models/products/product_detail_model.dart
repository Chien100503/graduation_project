class ProductDetailModel {
  final int id;
  final String name;
  final String brandName;
  final String typeName;
  final String description;
  final double price;
  final int stockQuantity;
  final String size;
  final double weight;
  final String expirationDate;
  final List<String> imageUrls;
  final DateTime createdAt;
  final DateTime updatedAt;
  final String categoryName;

  ProductDetailModel({
    required this.id,
    required this.name,
    required this.brandName,
    required this.typeName,
    required this.description,
    required this.price,
    required this.stockQuantity,
    required this.size,
    required this.weight,
    required this.expirationDate,
    required this.imageUrls,
    required this.createdAt,
    required this.updatedAt,
    required this.categoryName,
  });

  factory ProductDetailModel.fromJson(Map<String, dynamic> json) {
    return ProductDetailModel(
      id: json['id'],
      name: json['name'],
      brandName: json['brandName'],
      typeName: json['typeName'],
      description: json['description'],
      price: (json['price'] as num).toDouble(),
      stockQuantity: json['stockQuantity'],
      size: json['size'],
      weight: (json['weight'] as num).toDouble(),
      expirationDate: json['expirationDate'],
      imageUrls: List<String>.from(json['imageUrls'] ?? []),
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      categoryName: json['categoryName'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'brandName': brandName,
      'typeName': typeName,
      'description': description,
      'price': price,
      'stockQuantity': stockQuantity,
      'size': size,
      'weight': weight,
      'expirationDate': expirationDate,
      'imageUrls': imageUrls,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'categoryName': categoryName,
    };
  }

  @override
  String toString() {
    return 'ProductDetailModel(id: $id, name: $name, brand: $brandName, type: $typeName)';
  }
}
