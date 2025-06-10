import 'dart:convert';

import '../categories/breed_model.dart';
import '../categories/type_model.dart';

class ProductDetailModel {
  final int id;
  final String name;
  final String brandName;
  final String typeName;
  final String description;
  final double priceDiscount;
  final double price;
  final int stockQuantity;
  final String size;
  final double percentDiscount;
  final double weight;
  final String expirationDate;
  final String thumbnailUrl;
  final List<String> imageUrl;
  final TypeModel type;
  final String categoryName;
  final double rate;

  ProductDetailModel({
    required this.type,
    required this.rate,
    required this.priceDiscount,
    required this.percentDiscount,
    required this.thumbnailUrl,
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
    required this.imageUrl,
    required this.categoryName,
  });

  factory ProductDetailModel.fromJson(Map<String, dynamic> json) {
    return ProductDetailModel(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      brandName: json['brandName'] ?? '',
      typeName: json['typeName'] ?? '',
      description: json['description'] ?? '',
      price: (json['price'] as num?)?.toDouble() ?? 0.0,
      stockQuantity: (json['stockQuantity'] as num?)?.toInt() ?? 0,
      size: json['size'] ?? '',
      weight: (json['weight'] as num?)?.toDouble() ?? 0.0,
      expirationDate: json['expirationDate'] ?? '',
      imageUrl: (json['imageUrl'] is String)
          ? List<String>.from(jsonDecode(json['imageUrl']))
          : List<String>.from(json['imageUrl'] ?? []),
      categoryName: json['categoryName'] ?? '',
      thumbnailUrl: json['thumbnailUrl'] ?? '',
      rate: (json['rate'] as num?)?.toDouble() ?? 0.0,
      priceDiscount: (json['priceDiscount'] as num?)?.toDouble() ?? 0.0,
      percentDiscount: (json['percentDiscount'] as num?)?.toDouble() ?? 0.0,
      type: json['type'] != null
          ? TypeModel.fromJson(json['type'])
          : TypeModel(id: 0, name: 'Unknown',productCategoryId: 0),
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
      'imageUrl': imageUrl,
      'categoryName': categoryName,
      'thumbnailUrl': thumbnailUrl,
      'rate': rate,
      'priceDiscount': priceDiscount,
      'percentDiscount': percentDiscount
    };
  }

  @override
  String toString() {
    return 'ProductDetailModel(id: $id, name: $name, brand: $brandName, type: $typeName)';
  }
}
