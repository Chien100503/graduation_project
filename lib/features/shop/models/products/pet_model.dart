import 'package:pet_shop/features/shop/models/categories/breed_model.dart';

class PetModel {
  final int id;
  final String name;
  final double price;
  final BreedModel breed;
  // final String imageUrls;
  final String thumbnail;

  PetModel(
      {required this.id,
      required this.name,
      required this.price,
      // required this.imageUrls,
      required this.breed,
      required this.thumbnail});

  factory PetModel.fromJson(Map<String, dynamic> json) {
    // Kiểm tra các field bắt buộc
    if (json['id'] == null) throw Exception('Pet ID is null');
    if (json['name'] == null) throw Exception('Pet name is null');
    if (json['price'] == null) throw Exception('Pet price is null');
    // if (json['imageUrls'] == null) throw Exception('Pet imageUrl is null');
    if (json['breed'] == null) throw Exception('Pet breed is null');

    return PetModel(
      id: json['id'] is int ? json['id'] : int.parse(json['id'].toString()),
      name: json['name'].toString(),
      price: _parsePrice(json['price']),
      // imageUrls: json['imageUrl'].toString(),
      thumbnail: json['thumbnail'].toString(),
      breed: BreedModel.fromJson(json['breed'] as Map<String, dynamic>),
    );
  }

  // Helper method để parse price an toàn
  static double _parsePrice(dynamic price) {
    if (price == null) return 0.0;
    if (price is double) return price;
    if (price is int) return price.toDouble();
    if (price is String) {
      return double.tryParse(price) ?? 0.0;
    }
    return (price as num).toDouble();
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'price': price,
      // 'imageUrl': imageUrls,
      'thumbnail': thumbnail,
      'breed': {
        'id': breed.id,
        'name': breed.name,
      }
    };
  }

  @override
  String toString() {
    return 'PetModel('
        'id: $id, '
        'name: $name, '
        'price: $price, '
        // 'imageUrl: $imageUrls,'
        'thumbnail: $thumbnail, '
        'breed: ${breed.name})';
  }
}
