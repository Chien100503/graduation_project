import 'package:pet_shop/features/shop/models/categories/type_model.dart';

class ProductModel {
  final int id;
  final String name;
  // final double price;
  final TypeModel type;
  final String imageUrls;
  // final String thumbnail;

  ProductModel(
      {required this.id,
        required this.name,
        // required this.price,
        required this.imageUrls,
        required this.type,
        // required this.thumbnail
      });

  factory ProductModel.fromJson(Map<String, dynamic> json) {
    // Kiểm tra các field bắt buộc
    if (json['id'] == null) throw Exception('Pet ID is null');
    if (json['name'] == null) throw Exception('Pet name is null');
    // if (json['price'] == null) throw Exception('Product price is null');
    // if (json['imageUrls'] == null) throw Exception('Pet imageUrl is null');
    if (json['type'] == null) throw Exception('Product type is null');

    return ProductModel(
      id: json['id'] is int ? json['id'] : int.parse(json['id'].toString()),
      name: json['name'].toString(),
      // price: _parsePrice(json['price'] ?? int),
      imageUrls: json['imageUrl'].toString(),
      // thumbnail: json['thumbnail'].toString(),
      type: TypeModel.fromJson(json['type'] as Map<String, dynamic>),
    );
  }

  // Helper method để parse price an toàn
  static double _parsePrice(dynamic price) {
    if (price is double) return price;
    if (price is int) return price.toDouble();
    if (price is String) {
      return double.tryParse(price) ?? (throw Exception('Invalid price string'));
    }
    throw Exception('Invalid price type');
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      // 'price': price,
      'imageUrl': imageUrls,
      // 'thumbnail': thumbnail,
      'type': {
        'id': type.id,
        'name': type.name,
      }
    };
  }

  @override
  String toString() {
    return 'ProductModel('
        'id: $id, '
        'name: $name, '
        // 'price: $price, '
    'imageUrl: $imageUrls,'
        // 'thumbnail: $thumbnail, '
        'type: ${type.name})';
  }
}
